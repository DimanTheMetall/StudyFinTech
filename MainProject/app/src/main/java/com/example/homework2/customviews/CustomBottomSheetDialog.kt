package com.example.homework2.customviews

import android.content.Context
import com.example.homework2.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomBottomSheetDialog(context: Context) : BottomSheetDialog(context) {
    init {
        setContentView(R.layout.bottom_sheet_dialog_layout)
    }
}