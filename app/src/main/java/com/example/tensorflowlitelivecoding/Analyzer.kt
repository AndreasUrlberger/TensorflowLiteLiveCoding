package com.example.tensorflowlitelivecoding

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class Analyzer(context: Context, private val drawer: BoundingBoxDrawer) {

    private val cropWidth = 300
    private val cropHeight = 300

    private val detector: ObjectDetector

    init {
        val options =
            ObjectDetector.ObjectDetectorOptions.builder().setMaxResults(5).setScoreThreshold(0.5f)
                .build()
        detector = ObjectDetector.createFromFileAndOptions(context, "detect.tflite", options)
    }

    fun analyze(bitmap: Bitmap) {
        val cropped = bitmap.scale(cropWidth, cropHeight)
        val tensorImage = TensorImage.fromBitmap(cropped)
        val results = detector.detect(tensorImage)
        val boxes = results.map(Detection::getBoundingBox)
        val labels = results.map { result ->
            "${result.categories.first().label}(%.2f)".format(result.categories.first().score)
        }
        drawer.drawBoundingBoxes(boxes, labels)
    }
}