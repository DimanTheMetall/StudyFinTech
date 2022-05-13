package com.example.homework2.customviews

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.example.homework2.R
import com.example.homework2.dpToPx
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomReactionBottomSheetDialog(context: Context, val getEmoji: (String, String) -> Unit) :
    BottomSheetDialog(context) {

    private val emojiTriple = listOf(
        Triple(context.getString(R.string.emoji_1), "grinning", "U+1F600"),
        Triple(context.getString(R.string.emoji_2), "grinning_face_with_smiling_eyes", "U+1F601"),
        Triple(context.getString(R.string.emoji_3), "laughing", "U+1F606"),
        Triple(context.getString(R.string.emoji_4), "upside_down", "U+1F643"),
        Triple(context.getString(R.string.emoji_5), "innocent", "U+1F607"),
        Triple(context.getString(R.string.emoji_6), "heart_eyes", "U+1F60D"),
        Triple(context.getString(R.string.emoji_7), "smiling_face", "U+263A"),
        Triple(context.getString(R.string.emoji_8), "heart_kiss", "U+1F618"),
        Triple(context.getString(R.string.emoji_9), "heart", "U+2764"),
        Triple(context.getString(R.string.emoji_10), "money_face", "U+1F911"),
        Triple(context.getString(R.string.emoji_11), "neutral", "U+1F610"),
        Triple(context.getString(R.string.emoji_12), "sleeping", "U+1F634"),
        Triple(context.getString(R.string.emoji_13), "stuck_out_tongue_closed_eyes", "U+1F600"),
        Triple(context.getString(R.string.emoji_14), "+1", "U+1F44D")
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
