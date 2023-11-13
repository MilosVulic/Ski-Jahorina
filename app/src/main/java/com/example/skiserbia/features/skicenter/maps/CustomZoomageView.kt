package com.example.skiserbia.features.skicenter.maps

import android.content.Context
import android.util.AttributeSet
import com.jsibbold.zoomage.ZoomageView

class CustomZoomageView : ZoomageView {
    private var currentZoom: Float = 1.0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun canScrollHorizontally(direction: Int): Boolean {
        val bitmap = drawable
        if (bitmap != null) {
            val viewWidth = width
            val imageWidth = bitmap.intrinsicWidth
            val imageScale = currentZoom
            if (imageWidth * imageScale > viewWidth) {
                val x = -scrollX
                if (direction > 0) {
                    return x < imageWidth * imageScale - viewWidth
                } else if (direction < 0) {
                    return x > 0
                }
            }
        }
        return false
    }

    override fun canScrollVertically(direction: Int): Boolean {
        val bitmap = drawable
        if (bitmap != null) {
            val viewHeight = height
            val imageHeight = bitmap.intrinsicHeight
            val imageScale = currentZoom
            if (imageHeight * imageScale > viewHeight) {
                val y = -scrollY
                if (direction > 0) {
                    return y < imageHeight * imageScale - viewHeight
                } else if (direction < 0) {
                    return y > 0
                }
            }
        }
        return false
    }

    fun setCurrentZoom(zoom: Float) {
        currentZoom = zoom
    }

    fun getCurrentZoom(): Float {
        return currentZoom
    }
}
