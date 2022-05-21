package com.example.tensorflowlitelivetemplate

import android.app.Activity
import android.graphics.Bitmap
import android.os.Environment
import androidx.core.graphics.scale
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import java.io.FileOutputStream

class Analyzer(activity: Activity, private val drawer: BoundingBoxDrawer) {
    private val squish = true
    private val cropWidth = 300
    private val cropHeight = 300

    private val options: ObjectDetector.ObjectDetectorOptions =
        ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()

    private val detector: ObjectDetector = ObjectDetector.createFromFileAndOptions(
        activity,
        "detect.tflite", // must be same as the filename in assets folder
        options
    )

    private var isFirst = true;
    fun analyze(bitmap: Bitmap) {
        val cropped: Bitmap
        if (squish) {
            cropped = bitmap.scale(cropWidth, cropHeight)
        } else {
            val heightOffset = (bitmap.height - bitmap.width) / 2
            val resizedBmp: Bitmap =
                Bitmap.createBitmap(bitmap, 0, heightOffset, bitmap.width, bitmap.width)
            cropped = resizedBmp.scale(cropWidth, cropHeight)
        }

        /*if (isFirst) {
            isFirst = false
            save(cropped)
        }*/

        val tensorImage = TensorImage.fromBitmap(cropped)
        val results: List<Detection> = detector.detect(tensorImage)
        val boxes = results.map(Detection::getBoundingBox)
        val labels =
            results.map { result -> "${result.categories.first().label}(%.2f)".format(result.categories.first().score) }

        drawer.drawBoundingBoxes(boxes, labels, squish)
    }


    private fun save(bmp: Bitmap) {
        val filePath = Environment.getExternalStorageDirectory().absolutePath + "/Download"
        val dir = File(filePath)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "test.bmp")
        val fOut = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut)
        fOut.flush()
        fOut.close()
    }

}