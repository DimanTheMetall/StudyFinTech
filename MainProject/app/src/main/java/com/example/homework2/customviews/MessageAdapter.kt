package com.example.homework2.customviews

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.Message
import com.example.homework2.R
import com.example.homework2.databinding.CustomViewGroupLayoutBinding

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageHolder>() {
    private val messageList = ArrayList<Message>()
    lateinit var view: CustomViewGroup

    class MessageHolder(_item: ViewGroup) : RecyclerView.ViewHolder(_item) {
        private val binding = CustomViewGroupLayoutBinding.bind(_item)

        fun bind(item: Message) = with(binding) {
            messageTextView.text = item.textMessage
            messageTitleTextView.text = item.titleMessage
            avatarImageView.setImageResource(item.imageId)

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
        println("AAAA OnCreateViewHolder")
        view = CustomViewGroup(parent.context)

        return MessageAdapter.MessageHolder(view)
    }

    override fun onBindViewHolder(holder: MessageAdapter.MessageHolder, position: Int) {
        holder.bind(messageList[position])
        when (messageList[position].isYou) {
            true -> view.setRectangleColor(R.color.teal_700)
            false -> view.setRectangleColor(R.color.unselected)
        }
        println("AAAA OnBindViewHolder")
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addMessage(message: Message) {
        messageList.add(message)
        notifyDataSetChanged()
    }

}
