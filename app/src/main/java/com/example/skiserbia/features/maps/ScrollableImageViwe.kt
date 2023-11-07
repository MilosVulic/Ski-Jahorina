package com.example.skiserbia.features.maps

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView

class ScrollableImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    private val displayRect = Rect()
    private val scrollRect = Rect()
    private var lastX = 0f
    private var lastY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        displayRect.set(0, 0, width, height)
        scrollRect.set(0, 0, drawable?.intrinsicWidth ?: 0, drawable?.intrinsicHeight ?: 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - lastX
                val deltaY = y - lastY
                lastX = x
                lastY = y

                scrollBy(-deltaX.toInt(), -deltaY.toInt())
            }
            MotionEvent.ACTION_UP -> parent.requestDisallowInterceptTouchEvent(false)
        }

        restrictScrolling()
        return true
    }

    private fun restrictScrolling() {
        if (!scrollRect.contains(displayRect)) {
            if (displayRect.left < scrollRect.left) {
                scrollTo(scrollRect.left, scrollY)
            }
            if (displayRect.right > scrollRect.right) {
                scrollTo(scrollRect.right - displayRect.width(), scrollY)
            }
            if (displayRect.top < scrollRect.top) {
                scrollTo(scrollX, scrollRect.top)
            }
            if (displayRect.bottom > scrollRect.bottom) {
                scrollTo(scrollX, scrollRect.bottom - displayRect.height())
            }
        }
    }
}


