package com.example.homework2.customviews

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.example.homework2.Constants
import com.example.homework2.R
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.google.android.material.bottomsheet.BottomSheetDialog

class MessageCustomBottomSheetDialog(
    context: Context,
    private val onAddReactionClick: (SelectViewTypeClass.Message) -> Unit,
    private val onDeleteMessageClick: (SelectViewTypeClass.Message) -> Unit,
    private val onEditMessageClick: (SelectViewTypeClass.Message) -> Unit,
    private val onMoveMessageClick: (SelectViewTypeClass.Message) -> Unit,
    private val onCopyMessageClick: (SelectViewTypeClass.Message) -> Unit,
) : BottomSheetDialog(context) {

    private lateinit var message: SelectViewTypeClass.Message

    init {
        setContentView(R.layout.message_help_layout)
        setOnEditMessageClickListener()
        setOnAddReactionClickListener()
        setOnDeleteMessageClicker()
        setOnMoveClickListener()
        setOnCopyCLickListener()
    }

    fun setMessage(seatedMessage: SelectViewTypeClass.Message) {
        message = seatedMessage
    }

    private fun setOnAddReactionClickListener() {
        findViewById<ImageView?>(R.id.add_reaction_image_view)?.apply {
            setOnClickListener { onAddReactionClick.invoke(message) }
        }
    }

    private fun setOnDeleteMessageClicker() {
        findViewById<ImageView>(R.id.delete_message_image_view)?.apply {
            setOnClickListener {
                if (isNotYoursMessage()) toast(context.getString(R.string.dont_have_permission)) else onDeleteMessageClick.invoke(
                    message
                )
            }
        }
    }

    private fun setOnEditMessageClickListener() {
        findViewById<ImageView?>(R.id.edit_message_image_view)?.apply {
            setOnClickListener {
                if (isNotYoursMessage()) toast(context.getString(R.string.dont_have_permission)) else onEditMessageClick.invoke(
                    message
                )
            }
        }
    }

    private fun setOnMoveClickListener() {
        findViewById<ImageView?>(R.id.move_to_topic_image_view)?.apply {
            setOnClickListener {
                if (isNotYoursMessage()) toast(context.getString(R.string.dont_have_permission)) else onMoveMessageClick.invoke(
                    message
                )
            }
        }
    }

    private fun setOnCopyCLickListener() {
        findViewById<ImageView?>(R.id.copy_message_image_view).apply {
            this?.setOnClickListener {
                onCopyMessageClick.invoke(message)
                toast(context.getString(R.string.message_copied))
            }
        }
    }

    private fun toast(text: String) {
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun isNotYoursMessage(): Boolean = message.senderId != Constants.myId
}
