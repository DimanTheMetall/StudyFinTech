package com.example.homework2.customviews

import android.content.Context
import android.widget.Button
import android.widget.EditText
import com.example.homework2.R
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.google.android.material.bottomsheet.BottomSheetDialog

class EditMessageCustomBottomSheetDialog(
    context: Context,
    private val onApplyClick: (SelectViewTypeClass.Message) -> Unit,
    private val onCancelClick: () -> Unit
) : BottomSheetDialog(context) {

    private lateinit var message: SelectViewTypeClass.Message

    init {
        setContentView(R.layout.edit_message_layout)
        setOnApplyCLickListener()
        setOnCancelClickListener()

    }

    private fun setOnApplyCLickListener() {
        val applyButton: Button? = findViewById(R.id.apply_edit_message)
        val editMessageField: EditText? = findViewById(R.id.edit_message_field)

        applyButton?.setOnClickListener {
            onApplyClick.invoke(
                message.copy(
                    content = editMessageField?.text.toString()
                )
            )
        }
    }

    private fun setOnCancelClickListener() {
        val cancelButton: Button? = findViewById(R.id.cancel_edit_message)

        cancelButton?.setOnClickListener {
            onCancelClick.invoke()
        }
    }

    fun setMessage(seatedMessage: SelectViewTypeClass.Message) {
        val editMessageField: EditText? = findViewById(R.id.edit_message_field)
        message = seatedMessage
        editMessageField?.setText(message.content)
    }

}