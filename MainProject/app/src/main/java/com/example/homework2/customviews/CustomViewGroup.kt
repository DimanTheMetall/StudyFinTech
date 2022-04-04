package com.example.homework2.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.example.homework2.R
import com.example.homework2.dataclasses.Reaction

class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private val imageView by lazy { getChildAt(0) }
    private val messageTitle by lazy { getChildAt(1) }
    private val message by lazy { getChildAt(2) }
    private val flexBox by lazy { getChildAt(3) as CustomFlexBox }
    private var isYours: Boolean = true
    private var onAddEmojiCLick: (Reaction ) -> Unit = {}

    init {
        inflate(context, R.layout.custom_view_group_layout, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

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
                (messageTitle.measuredHeightWithMargins +
                        message.measuredHeightWithMargins +
                        flexBox.measuredHeightWithMargins)
            )
        } else {
            message.measuredHeightWithMargins + flexBox.measuredHeightWithMargins
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
                imageView.measuredWidthWithMargins + message.measuredWidthWithMargins + message.marginRight,
                messageTitle.measuredHeightWithMargins + message.measuredHeightWithMargins
            )
            flexBox.layout(
                imageView.measuredWidthWithMargins + flexBox.marginLeft,
                message.bottom + flexBox.marginTop,
                imageView.measuredWidthWithMargins + flexBox.measuredWidthWithMargins,
                message.bottom + flexBox.measuredHeightWithMargins
            )
        } else {
            message.layout(
                (parent as View).right - message.measuredWidthWithMargins,
                message.marginTop,
                (parent as View).measuredWidthWithMargins,
                message.measuredHeightWithMargins
            )
            flexBox.layout(
                message.right - flexBox.measuredWidthWithMargins,
                message.measuredHeightWithMargins + flexBox.marginTop,
                message.right,
                flexBox.top + flexBox.measuredWidthWithMargins
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

    fun clearEmoji() {
        if (flexBox.childCount > 1) flexBox.removeViews(0, flexBox.childCount - 1)
        requestLayout()
    }

    override fun dispatchDraw(canvas: Canvas?) {
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
                (parent as View).right - message.measuredWidthWithMargins.toFloat(),
                message.top.toFloat(),
                message.right.toFloat(),
                message.top.toFloat() + message.measuredHeightWithMargins,
                30f, 30f,
                paintRectRound
            )
        }
        super.dispatchDraw(canvas)
    }

    fun addEmoji(reaction: Reaction, count: Int) {

        flexBox.addView(
            CustomTextView(ContextThemeWrapper(context, R.style.CustomTextView))
                .apply {
                    layoutParams =
                        ViewGroup.MarginLayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(
                                context.dpToPx(0),
                                context.dpToPx(6),
                                context.dpToPx(10),
                                context.dpToPx(0)
                            )
                        }

                    setOnClickListener { view ->
                        view.isSelected = !view.isSelected
                        onAddEmojiCLick.invoke(reaction)
                    }

                    setEmojiNumberOnView(count)
                    setEmojiOnView(String(Character.toChars(reaction.emoji_code.toInt(16))))
                }, flexBox.childCount - 1
        )
    }

    fun setOnEmojiClickListener(l: (Reaction) -> Unit) {
        onAddEmojiCLick = l
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
