package com.example.homework2.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.homework2.R

class CustomTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val textPaint = TextPaint().apply {
        isAntiAlias = true
    }

    private var text = ""
    private var emoji: String = "\uD83E\uDD70"
    private var emojiNumber: Int = 0

    private val tempBound = Rect()
    private val tempTextPoint = PointF()

    private var sumWidth = 0
    private var sumHeight = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView)

        textPaint.textSize =
            typedArray.getDimension(R.styleable.CustomTextView_customTextSize, 50f)
        textPaint.color =
            typedArray.getColor(R.styleable.CustomTextView_customTextColor, Color.BLACK)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        text = "$emoji $emojiNumber"
        textPaint.getTextBounds(text, 0, text.length, tempBound)

        val textWidth = tempBound.width()
        val textHeight = tempBound.height()

        sumWidth = textWidth + paddingLeft + paddingRight
        sumHeight = textHeight + paddingTop + paddingBottom

        val resultWidth = resolveSize(sumWidth, widthMeasureSpec)
        val resultHeight = resolveSize(sumHeight, heightMeasureSpec)

        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        tempTextPoint.y = h / 2f + tempBound.height() / 2f - textPaint.descent()
        tempTextPoint.x = w / 2f - tempBound.width() / 2f
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawText(text, tempTextPoint.x, tempTextPoint.y, textPaint)

    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + SUPPORTED_DRAWABLE_STATE.size)
        if (isSelected) {
            mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        }
        return drawableState
    }

    fun setEmojiOnView(emo: String) {
        emoji = emo
        requestLayout()
    }

    fun setEmojiNumberOnView(number: Int) {
        emojiNumber = number
        requestLayout()
    }

    companion object {
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
    }
}
