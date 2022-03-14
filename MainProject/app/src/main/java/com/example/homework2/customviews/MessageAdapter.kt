package com.example.homework2.customviews

import android.content.Context
import android.view.Gravity
import android.view.Gravity.RIGHT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.Message
import com.example.homework2.R
import com.example.homework2.databinding.CustomViewGroupLayoutBinding

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageHolder>() {
    private val messageList = ArrayList<Message>()


    class MessageHolder(private val customViewGroup: CustomViewGroup) :
        RecyclerView.ViewHolder(customViewGroup) {
        private val binding = CustomViewGroupLayoutBinding.bind(customViewGroup)

        fun bind(item: Message) = with(binding) {
            messageTextView.text = item.textMessage
            messageTitleTextView.text = item.titleMessage
            avatarImageView.setImageResource(item.imageId)

            when (item.isYou) {
                true -> {
                    customViewGroup.setRectangleColor(R.color.teal_700)
                    customViewGroup.setYoursMessage(true)
                }
                false -> {
                    customViewGroup.setRectangleColor(R.color.unselected)
                    customViewGroup.setYoursMessage(false)
                }
            }
            println("AAAA bind")

            if (item.isYou) {
                avatarImageView.isVisible = false
                messageTitleTextView.layoutParams.height = 0
                messageTitleTextView.text = ""
            } else {
                avatarImageView.isVisible = true
                messageTitleTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageAdapter.MessageHolder {

        return MessageAdapter.MessageHolder(CustomViewGroup(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })
    }

    override fun onBindViewHolder(holder: MessageAdapter.MessageHolder, position: Int) {
        holder.bind(messageList[position])

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addMessage(message: Message) {
        messageList.add(message)
        notifyDataSetChanged()
    }

}
