package com.example.homework2.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import com.example.homework2.measuredHeightWithMargins
import com.example.homework2.measuredWidthWithMargins

class CustomFlexBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var maxWidth = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var totalHeight = 0
        var usedWidth = 0
        var maxUsedWidth = 0

        maxWidth = MeasureSpec.getSize(widthMeasureSpec)

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            measureChildWithMargins(
                child,
                widthMeasureSpec,
                usedWidth,
                heightMeasureSpec,
                totalHeight
            )
            if (totalHeight==0) totalHeight = child.measuredHeightWithMargins

            usedWidth += child.measuredWidthWithMargins
            if (maxUsedWidth < usedWidth) maxUsedWidth = usedWidth
            if (maxWidth - usedWidth < child.measuredWidthWithMargins) {

                usedWidth = 0
                totalHeight += child.measuredHeightWithMargins
            }
        }

        when (childCount) {
            1 -> setMeasuredDimension(
                resolveSize(0, widthMeasureSpec),
                resolveSize(0, heightMeasureSpec)
            )
            else -> setMeasuredDimension(
                resolveSize(maxUsedWidth, widthMeasureSpec),
                resolveSize(totalHeight, heightMeasureSpec)
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedWidth = 0
        var currentHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)

            child.layout(
                usedWidth + child.marginLeft,
                currentHeight + child.marginTop,
                usedWidth + child.measuredWidth,
                currentHeight + child.measuredHeightWithMargins
            )

            usedWidth += child.measuredWidthWithMargins

            if (maxWidth - usedWidth < child.measuredWidthWithMargins) {
                usedWidth = 0
                currentHeight += child.measuredHeightWithMargins
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }
}
