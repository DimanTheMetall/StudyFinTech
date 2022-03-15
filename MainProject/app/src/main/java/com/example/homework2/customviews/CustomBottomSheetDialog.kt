package com.example.homework2.customviews

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.homework2.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomBottomSheetDialog(context: Context, val getEmoji: (String) -> Unit) :
    BottomSheetDialog(context) {

    init {
        setContentView(R.layout.bottom_sheet_dialog_layout)
        val viewGroup = findViewById<ViewGroup>(R.id.grid_emoji)
        if (viewGroup != null)
            for (emojiIndex in 0 until viewGroup.childCount) {
                val childEmoji = viewGroup.getChildAt(emojiIndex) as TextView
                childEmoji.setOnClickListener {
                    getEmoji(childEmoji.text.toString())
                }
            }
    }
}