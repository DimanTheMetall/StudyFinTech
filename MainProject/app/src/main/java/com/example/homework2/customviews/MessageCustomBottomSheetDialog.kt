package com.example.homework2.customviews

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.example.homework2.R
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.google.android.material.bottomsheet.BottomSheetDialog

class MessageCustomBottomSheetDialog(
    context: Context,
    val onAddReactionClick: (SelectViewTypeClass.Message) -> Unit,
    val onDeleteReactionClick: (SelectViewTypeClass.Message) -> Unit,
    val onEditMessageClickL: (SelectViewTypeClass.Message) -> Unit,
    val onMoveMessageClick: (SelectViewTypeClass.Message) -> Unit,
    val onCopyMessageClick: (SelectViewTypeClass.Message) -> Unit
) : BottomSheetDialog(context) {

    private lateinit var message: SelectViewTypeClass.Message
    private val addReactionView = findViewById<ImageView>(R.id.add_reaction_image_view)
    private val deleteMessageView = findViewById<ImageView>(R.id.delete_message_image_view)
    private val editMessageView = findViewById<ImageView>(R.id.edit_message_image_view)
    private val moveMessageView = findViewById<ImageView>(R.id.move_to_topic_image_view)
    private val copyMessageView = findViewById<ImageView>(R.id.copy_message_image_view)

    private val editMessageField = findViewById<EditText>(R.id.edit_message_field)
    private val editMessageModule = findViewById<View>(R.id.edit_message_module)


    init {
        setContentView(R.layout.message_help_layout)
    }

    private fun setOnEditMessageClickListener() {
        editMessageView?.setOnClickListener {
            if (editMessageModule?.visibility == View.GONE) {
                editMessageModule.visibility = View.VISIBLE
            } else {
                editMessageModule?.visibility = View.GONE
            }
        }
    }

    fun setMessage(seatedMessage: SelectViewTypeClass.Message) {
        message = seatedMessage
    }


}
