package com.example.tensorflowlitelivecoding

import android.app.Activity
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.example.tensorflowlitelivecoding.databinding.FragmentCameraBinding
import kotlin.math.roundToInt

class BoundingBoxDrawer (binding: FragmentCameraBinding, private val activity: Activity) {
    private val boundingContainer = binding.boundingBoxes
    private val boundingRects = listOf(
        binding.boundingRect0,
        binding.boundingRect1,
        binding.boundingRect2,
        binding.boundingRect3,
        binding.boundingRect4
    )
    private val labelViews = listOf(
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

    fun drawBoundingBoxes(boxes: List<RectF>, labels: List<String>, squish: Boolean = true) {
        val heightFactor: Double
        val widthFactor: Double
        val heightOffset: Int
        if(squish){
            heightFactor = viewHeight / cropHeight
            widthFactor = viewWidth / cropWidth
            heightOffset = 0
        }else{
            heightFactor = viewWidth / cropWidth
            widthFactor = viewWidth / cropWidth
            heightOffset = (viewHeight - viewWidth) / 2
        }
        val constraints = ConstraintSet()
        constraints.clone(boundingContainer)
        boxes.forEachIndexed { index, bounds ->
            val bounding = boundingRects[index]

            val scaledBounds = Rect().apply {
                top = (bounds.top * heightFactor).roundToInt() + heightOffset
                bottom = (bounds.bottom * heightFactor).roundToInt() + heightOffset
                left = (bounds.left * widthFactor).roundToInt()
                right = (bounds.right * widthFactor).roundToInt()
            }

            constraints.constrainWidth(bounding.id, scaledBounds.width())
            constraints.constrainHeight(bounding.id, scaledBounds.height())
            constraints.setMargin(bounding.id, ConstraintSet.START, scaledBounds.left)
            constraints.setMargin(bounding.id, ConstraintSet.TOP, scaledBounds.top)
            constraints.setVisibility(bounding.id, View.VISIBLE)
        }

        activity.runOnUiThread {
            labels.forEachIndexed { index, label ->
                labelViews[index].text = label
            }
            constraints.applyTo(boundingContainer)
            for (i in boxes.size until boundingRects.size) {
                boundingRects[i].visibility = View.GONE
            }
        }
    }
}