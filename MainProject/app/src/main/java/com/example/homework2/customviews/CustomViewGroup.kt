package com.example.homework2.customviews

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.*
import com.example.homework2.R
import kotlin.math.max

class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var imageView = getChildAt(0)
    private var messageTitle = getChildAt(1)
    private var message = getChildAt(2)
    private var flexBox = getChildAt(3)


    init {
        inflate(context, R.layout.custom_view_group_layout, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        indexAllChild()

        measureChildWithMargins(imageView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        measureChildWithMargins(
            messageTitle,
            widthMeasureSpec,
            messageTitle.measuredWidthWithMargins,
            heightMeasureSpec,
            messageTitle.marginTop
        )
        measureChildWithMargins(
            message,
            widthMeasureSpec,
            imageView.measuredWidthWithMargins,
            heightMeasureSpec,
            messageTitle.measuredHeightWithMargins
        )
        measureChildWithMargins(
            flexBox,
            widthMeasureSpec,
            imageView.measuredWidthWithMargins,
            heightMeasureSpec,
            messageTitle.measuredHeightWithMargins + message.measuredHeightWithMargins
        )

        val groupWidth = imageView.measuredWidthWithMargins + maxOf(
            messageTitle.measuredWidthWithMargins,
            message.measuredWidthWithMargins,
        )
        val groupHeight = maxOf(
            imageView.measuredHeightWithMargins,
            (messageTitle.measuredHeightWithMargins + message.measuredHeightWithMargins + flexBox.measuredHeightWithMargins)
        )

        setMeasuredDimension(
            resolveSize(groupWidth, widthMeasureSpec),
            resolveSize(groupHeight, heightMeasureSpec)
        )

    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        imageView.layout(
            imageView.marginLeft,
            imageView.marginTop,
            imageView.measuredWidthWithMargins,
            imageView.measuredHeightWithMargins
        )

        messageTitle.layout(
            imageView.measuredWidthWithMargins + messageTitle.marginLeft,
            messageTitle.marginTop,
            imageView.measuredWidthWithMargins + messageTitle.measuredWidthWithMargins,
            messageTitle.measuredWidthWithMargins
        )
        message.layout(
            imageView.measuredWidthWithMargins + message.marginLeft,
            messageTitle.measuredHeightWithMargins + message.marginTop,
            imageView.measuredWidthWithMargins + message.measuredWidth + message.marginRight,
            messageTitle.measuredHeightWithMargins + message.measuredHeightWithMargins
        )
        flexBox.layout(
            imageView.measuredWidthWithMargins + flexBox.marginLeft,
            message.bottom + flexBox.marginTop,
            imageView.measuredWidthWithMargins + flexBox.measuredWidthWithMargins,
            message.bottom + flexBox.measuredHeight + flexBox.marginBottom
        )

    }

    private val paintRectRound = Paint().apply {
       color =  ContextCompat.getColor(context, R.color.unselected)
    }


    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.drawRoundRect(
            imageView.right.toFloat(),
            imageView.top.toFloat(),
            maxOf(messageTitle.right.toFloat(), message.right.toFloat()),
            maxOf(imageView.bottom.toFloat(), message.bottom.toFloat()),
            30f, 30f,
            paintRectRound
        )
        super.dispatchDraw(canvas)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    val View.measuredWidthWithMargins: Int
        get() {
            val params = layoutParams as MarginLayoutParams
            return measuredWidth + params.rightMargin + params.leftMargin
        }
    val View.measuredHeightWithMargins: Int
        get() {
            val params = layoutParams as MarginLayoutParams
            return measuredHeight + params.topMargin + params.bottomMargin
        }

    private fun indexAllChild() {
        imageView = getChildAt(0)
        messageTitle = getChildAt(1)
        message = getChildAt(2)
        flexBox = getChildAt(3)
    }

}