package com.example.tensorflowlitelivetemplate

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.example.tensorflowlitelivetemplate.databinding.FragmentCameraBinding
import org.tensorflow.lite.task.vision.detector.Detection
import kotlin.math.roundToInt

class DetectionDrawer(binding: FragmentCameraBinding, private val activity: Activity) {
    private val labelMap: List<String> = listOf(
        "person",
        "bicycle",
        "car",
        "motorcycle",
        "airplane",
        "bus",
        "train",
        "truck",
        "boat",
        "traffic light",
        "fire hydrant",
        "???",
        "stop sign",
        "parking meter",
        "bench",
        "bird",
        "cat",
        "dog",
        "horse",
        "sheep",
        "cow",
        "elephant",
        "bear",
        "zebra",
        "giraffe",
        "???",
        "backpack",
        "umbrella",
        "???",
        "???",
        "handbag",
        "tie",
        "suitcase",
        "frisbee",
        "skis",
        "snowboard",
        "sports ball",
        "kite",
        "baseball bat",
        "baseball glove",
        "skateboard",
        "surfboard",
        "tennis racket",
        "bottle",
        "???",
        "wine glass",
        "cup",
        "fork",
        "knife",
        "spoon",
        "bowl",
        "banana",
        "apple",
        "sandwich",
        "orange",
        "broccoli",
        "carrot",
        "hot dog",
        "pizza",
        "donut",
        "cake",
        "chair",
        "couch",
        "potted plant",
        "bed",
        "???",
        "dining table",
        "???",
        "???",
        "toilet",
        "???",
        "tv",
        "laptop",
        "mouse",
        "remote",
        "keyboard",
        "cell phone",
        "microwave",
        "oven",
        "toaster",
        "sink",
        "refrigerator",
        "???",
        "book",
        "clock",
        "vase",
        "scissors",
        "teddy bear",
        "hair drier",
        "toothbrush"
    )
    private val boundingContainer = binding.boundingBoxes
    private val boundingRects = listOf(
        binding.boundingRect0,
        binding.boundingRect1,
        binding.boundingRect2,
        binding.boundingRect3,
        binding.boundingRect4
    )
    private val labels = listOf(
        binding.boundingRectName0,
        binding.boundingRectName1,
        binding.boundingRectName2,
        binding.boundingRectName3,
        binding.boundingRectName4
    )
    private val viewHeight = binding.viewFinder.height
    private val viewWidth = binding.viewFinder.width
    private val cropWidth = 300.0
    private val cropHeight = 300.0
    private val heightFactor = viewHeight / cropHeight
    private val widthFactor = viewWidth / cropWidth

    fun drawDetections(detections: List<Detection>) {
        val constraints = ConstraintSet()
        constraints.clone(boundingContainer)
        val labelsToSet = mutableListOf<String>()

        val sDetections =
            detections.sortedByDescending { if (it.categories.isEmpty()) 0.0f else it.categories.first().score }
        sDetections.forEachIndexed { index, detection ->
            val bounding = boundingRects[index]

            val scaledBounds = Rect().apply {
                val bounds = detection.boundingBox
                top = (bounds.top * heightFactor).roundToInt()
                bottom = (bounds.bottom * heightFactor).roundToInt()
                left = (bounds.left * widthFactor).roundToInt()
                right = (bounds.right * widthFactor).roundToInt()
            }

            constraints.constrainWidth(bounding.id, scaledBounds.width())
            constraints.constrainHeight(bounding.id, scaledBounds.height())
            constraints.setMargin(bounding.id, ConstraintSet.START, scaledBounds.left)
            constraints.setMargin(bounding.id, ConstraintSet.TOP, scaledBounds.top)
            constraints.setVisibility(bounding.id, View.VISIBLE)

            val label = labelMap[detection.categories.first().index]
            val score = detection.categories.first().score
            labelsToSet.add("$label(%.2f)".format(score))
        }

        activity.runOnUiThread {
            labelsToSet.forEachIndexed { index, label ->
                labels[index].text = label
            }
            constraints.applyTo(boundingContainer)
            for (i in sDetections.size until labels.size) {
                boundingRects[i].visibility = View.GONE
            }
        }
    }
}