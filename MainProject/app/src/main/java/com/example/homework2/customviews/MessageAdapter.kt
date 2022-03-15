package com.example.homework2.customviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.R
import com.example.homework2.databinding.CustomViewGroupLayoutBinding
import com.example.homework2.databinding.TimeTvBinding

class MessageAdapter(val onLongTap: (Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messageList = ArrayList<SelectViewTypeClass>()

    private enum class MessageType {
        MESSAGE, DATA
    }

    override fun getItemViewType(position: Int): Int {
        return when (messageList[position]) {
            is SelectViewTypeClass.Data -> MessageType.DATA.ordinal
            is SelectViewTypeClass.Message -> MessageType.MESSAGE.ordinal
        }
    }

    class DataHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val timeViewBinding = TimeTvBinding.bind(view)
        fun bind(time: String) {
            timeViewBinding.timeTv.text = time
        }

    }

    inner class MessageHolder(private val customViewGroup: CustomViewGroup) :
        RecyclerView.ViewHolder(customViewGroup) {
        private val binding = CustomViewGroupLayoutBinding.bind(customViewGroup)

        fun bind(item: SelectViewTypeClass.Message) = with(binding) {
            messageTextView.text = item.textMessage
            messageTitleTextView.text = item.titleMessage
            avatarImageView.setImageResource(item.imageId)

            customViewGroup.clearEmoji()

            item.emoji.forEach {
                customViewGroup.addEmoji(it.emoji, it.count)
            }

            binding.root.setOnLongClickListener {
                onLongTap.invoke(adapterPosition)
                true
            }

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
    ): RecyclerView.ViewHolder {
        return when (MessageType.values()[viewType]) {
            MessageType.MESSAGE -> MessageHolder(CustomViewGroup(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            })
            MessageType.DATA -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.time_tv, parent, false)
                DataHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val inform = messageList[position]) {
            is SelectViewTypeClass.Data -> (holder as DataHolder).bind(inform.time)
            is SelectViewTypeClass.Message -> {
                (holder as MessageHolder).bind(inform)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addMessage(message: SelectViewTypeClass) {
        messageList.add(message)
        notifyDataSetChanged()
    }

    fun addEmojiReaction(emoji: String, position: Int) {
        (messageList[position] as? SelectViewTypeClass.Message)?.emoji?.add(Reaction(emoji, 0))
        notifyItemChanged(position)
    }

    fun removeEmoji(position: Int) {
        (messageList[position] as? SelectViewTypeClass.Message)?.emoji?.removeLast()
        notifyItemChanged(position)
    }
}
