package com.example.tensorflowlitelivecoding

import android.app.Activity
import android.graphics.Bitmap
import androidx.core.graphics.scale
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class Analyzer(activity: Activity, private val drawer: BoundingBoxDrawer) {
    private val squish = true
    private val cropWidth = 300
    private val cropHeight = 300

    private val detector: ObjectDetector

    init {
        val options: ObjectDetector.ObjectDetectorOptions =
            ObjectDetector.ObjectDetectorOptions.builder()
                .setMaxResults(5)
                .setScoreThreshold(0.5f)
                .build()

        detector = ObjectDetector.createFromFileAndOptions(
            activity,
            "detect.tflite", // must be same as the filename in assets folder
            options
        )
    }

    fun analyze(bitmap: Bitmap) {
        val cropped: Bitmap = if (squish) {
            bitmap.scale(cropWidth, cropHeight)
        } else {
            val heightOffset = (bitmap.height - bitmap.width) / 2
            val resizedBmp: Bitmap =
                Bitmap.createBitmap(bitmap, 0, heightOffset, bitmap.width, bitmap.width)
            resizedBmp.scale(cropWidth, cropHeight)
        }

        val tensorImage = TensorImage.fromBitmap(cropped)
        val results: List<Detection> = detector.detect(tensorImage)
        val boxes = results.map(Detection::getBoundingBox)
        val labels =
            results.map { result -> "${result.categories.first().label}(%.2f)".format(result.categories.first().score) }

        drawer.drawBoundingBoxes(boxes, labels, squish)
    }
}