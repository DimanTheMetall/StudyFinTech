package com.example.homework2.customviews

import android.content.Context
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.homework2.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog

class StreamsCustomBottomSheetDialog(
    context: Context,
    private val onCreateCLick: (String, String?) -> Unit,
    private val onCancelClick: () -> Unit
) : BottomSheetDialog(context) {


    init {
        setContentView(R.layout.streams_bottom_sheet_dialog_layout)
        hideShimmer()
        setOnCancelClickListener()
        setOnCreateClickListener(
            streamName = findViewById<TextView>(R.id.stream_name_edit_text)?.text.toString(),
            streamDescription = findViewById<TextView>(R.id.stream_description_edit_text)?.text.toString()
        )

    }

    private fun setOnCreateClickListener(
        streamName: String?,
        streamDescription: String?
    ) {

        findViewById<Button>(R.id.create_stream_button)?.setOnClickListener {
            if (streamName.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.please_write_stream_name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                onCreateCLick.invoke(streamName, streamDescription)
            }
        }

    }

    private fun setOnCancelClickListener() {
        findViewById<Button>(R.id.cancel_stream_button)?.setOnClickListener {
            onCancelClick.invoke()
        }
    }

    fun hideShimmer() {
        findViewById<ShimmerFrameLayout>(R.id.stream_bottom_sheet_shimmer)?.hideShimmer()
    }

    fun startShimmer() {
        findViewById<ShimmerFrameLayout>(R.id.stream_bottom_sheet_shimmer)?.showShimmer(true)
    }

}
