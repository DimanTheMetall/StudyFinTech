package com.example.homework2.customviews

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.example.homework2.Constance
import com.example.homework2.R
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.google.android.material.bottomsheet.BottomSheetDialog

class MessageCustomBottomSheetDialog(
    context: Context,
    private val onAddReactionClick: (SelectViewTypeClass.Message) -> Unit,
    val onDeleteMessageClick: (SelectViewTypeClass.Message) -> Unit,
    private val onEditMessageClick: (SelectViewTypeClass.Message) -> Unit,
    val onMoveMessageClick: (SelectViewTypeClass.Message) -> Unit,
    val onCopyMessageClick: (SelectViewTypeClass.Message) -> Unit
) : BottomSheetDialog(context) {

    private lateinit var message: SelectViewTypeClass.Message
    private var moveMessageView: ImageView? = null
    private var copyMessageView: ImageView? = null


    init {
        setContentView(R.layout.message_help_layout)
        setOnEditMessageClickListener()
        setOnAddReactionClickListener()
        setOnDeleteMessageClicker()
    }


    fun setMessage(seatedMessage: SelectViewTypeClass.Message) {
        message = seatedMessage
    }

    private fun setOnAddReactionClickListener() {
        val addReactionView: ImageView? = findViewById(R.id.add_reaction_image_view)

        addReactionView?.setOnClickListener { onAddReactionClick.invoke(message) }
    }

    private fun setOnDeleteMessageClicker() {
        val deleteMessageView: ImageView? = findViewById(R.id.delete_message_image_view)

        deleteMessageView?.setOnClickListener {

            if (message.senderId != Constance.myId) {
                toastAboutPermission()
            } else {
                onDeleteMessageClick.invoke(message)
            }

        }
    }

    private fun setOnEditMessageClickListener() {
        val editMessageView: ImageView? = findViewById(R.id.edit_message_image_view)

        editMessageView?.setOnClickListener {
            if (message.senderId != Constance.myId) {
                toastAboutPermission()
            } else {
                onEditMessageClick.invoke(message)
            }
        }
    }

    private fun toastAboutPermission() {
        Toast.makeText(
            context,
            context.getString(R.string.dont_have_permission),
            Toast.LENGTH_SHORT
        ).show()
    }

}
