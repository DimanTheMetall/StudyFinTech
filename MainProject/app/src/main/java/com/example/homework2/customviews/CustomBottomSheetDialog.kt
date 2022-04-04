package com.example.homework2.customviews

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.example.homework2.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomBottomSheetDialog(context: Context, val getEmoji: (String, String) -> Unit) :
    BottomSheetDialog(context) {

    private val emojiTriple = listOf<Triple<String, String, String>>(
        Triple(context.getString(R.string.emoji_1), "grinning", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600"),
        Triple(context.getString(R.string.emoji_1), "grinning face", "U+1F600")
    )

    init {
        setContentView(R.layout.bottom_sheet_dialog_layout)
        val viewGroup = findViewById<ViewGroup>(R.id.grid_emoji)
        if (viewGroup != null) {
            viewGroup.removeAllViews()
            emojiTriple.forEach { triple ->
                val textView = AppCompatTextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    text = triple.first
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
                    setPadding(
                        context.dpToPx(6),
                        context.dpToPx(6),
                        context.dpToPx(6),
                        context.dpToPx(6)
                    )

                    setOnClickListener { getEmoji(triple.second, triple.third) }
                }
                viewGroup.addView(textView)
            }
        }
    }
}
