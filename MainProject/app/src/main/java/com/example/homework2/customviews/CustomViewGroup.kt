package com.example.homework2.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.example.homework2.R

class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private val imageView by lazy { getChildAt(0) }
    private val messageTitle by lazy { getChildAt(1) }
    private val message by lazy { getChildAt(2) }
    private val flexBox by lazy { getChildAt(3) as CustomFlexBox }
    private var isYours: Boolean = true

    init {
        inflate(context, R.layout.custom_view_group_layout, this)
    }

    fun addEmoji(emoji: String, number: Int) {
        flexBox.addView(CustomTextView(ContextThemeWrapper(context, R.style.CustomTextView)).apply {
            layoutParams =
                MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    setMargins(
                        context.dpToPx(0),
                        context.dpToPx(6),
                        context.dpToPx(10),
                        context.dpToPx(0)
                    )
                }
            setOnClickListener { view ->
                view.isSelected = !view.isSelected
            }
            setEmojiNumberOnView(number)
            setEmojiOnView(emoji)
        }, flexBox.childCount - 1)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        println("AAAA On MEASURED")

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
        val groupHeight = if (!isYours) {
            maxOf(
                imageView.measuredHeightWithMargins,
                (messageTitle.measuredHeightWithMargins + message.measuredHeightWithMargins + flexBox.measuredHeightWithMargins)
            )
        } else {
            message.measuredHeightWithMargins
        }

        setMeasuredDimension(
            resolveSize(groupWidth, widthMeasureSpec),
            resolveSize(groupHeight, heightMeasureSpec)
        )

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!isYours) {
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

        } else {

            message.layout(
                message.measuredWidthWithMargins,
                message.marginTop,
                (parent as View).measuredWidth,
                message.measuredHeightWithMargins
            )
            flexBox.layout(
                flexBox.measuredWidthWithMargins,
                message.bottom + flexBox.marginTop,
                flexBox.marginRight,
                message.bottom + flexBox.measuredHeight + flexBox.marginBottom
            )
        }
    }

    private var paintRectRound = Paint().apply {
        color = ContextCompat.getColor(context, R.color.unselected)
    }

    fun setRectangleColor(colorId: Int) {
        paintRectRound = Paint().apply {
            color = ContextCompat.getColor(context, colorId)
            invalidate()
        }
    }

    fun setYoursMessage(isYou: Boolean) {
        isYours = isYou
        requestLayout()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        println("AAAA despatchDraw ")
        if (!isYours) {
            canvas?.drawRoundRect(
                imageView.right.toFloat(),
                imageView.top.toFloat(),
                maxOf(messageTitle.right.toFloat(), message.right.toFloat()),
                maxOf(imageView.bottom.toFloat(), message.bottom.toFloat()),
                30f, 30f,
                paintRectRound
            )
        } else {
            canvas?.drawRoundRect(
                message.measuredWidthWithMargins.toFloat(),
                message.top.toFloat(),
                message.right.toFloat(),
                messageTitle.bottom.toFloat() + message.measuredHeight ,
                30f, 30f,
                paintRectRound
            )
        }
        super.dispatchDraw(canvas)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is ViewGroup.MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return ViewGroup.MarginLayoutParams(p)
    }

}
