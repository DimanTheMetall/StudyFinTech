package com.example.homework2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework2.customviews.CustomViewGroup
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.databinding.CustomViewGroupLayoutBinding
import com.example.homework2.databinding.TimeTvBinding
import com.example.homework2.dataclasses.Reaction

class MessageAdapter(
    val onTab: (Int) -> Unit,
    val onEmoji: (Int, Reaction) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(this, DiffCallback())

    private enum class MessageType {
        MESSAGE, DATA
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is SelectViewTypeClass.Chat.Date -> MessageType.DATA.ordinal
            is SelectViewTypeClass.Chat.Message -> MessageType.MESSAGE.ordinal
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

        fun bind(item: SelectViewTypeClass.Chat.Message) = with(binding) {
            messageTextView.text = item.content
            messageTitleTextView.text = item.sender_full_name


            if (!item.avatar_url.isNullOrEmpty()) {
                Glide.with(customViewGroup.context)
                    .load(item.avatar_url)
                    .placeholder(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(binding.avatarImageView)
            } else {
                binding.avatarImageView.setImageResource(R.mipmap.ic_launcher)
            }

//            customFlexBox.getChildAt(customFlexBox.childCount - 1)
//                .setOnClickListener {
//                    onTab.invoke(adapterPosition)
//                }
//
            customViewGroup.clearEmoji()

            with(item) {
                var isAdded = false
                for (currentReaction in reactions.indices) {
                    for (pastIndex in 0 until currentReaction) {
                        if (reactions[currentReaction].emoji_code == reactions[pastIndex].emoji_code) {
                            isAdded = true
                        }
                    }

                    var emojiCount = 1

                    for (i in currentReaction until reactions.lastIndex) {
                        if (reactions[currentReaction].emoji_code == reactions[i].emoji_code) {
                            emojiCount++
                        }
                    }
                    if (!isAdded) {
                        customViewGroup.addEmoji(reactions[currentReaction], emojiCount)
                    }
                }
            }
//
//            binding.root.setOnLongClickListener {
//                onTab.invoke(adapterPosition)
//                true
//            }
//
//            (binding.root as CustomViewGroup).setOnEmojiClickListener {
//                onEmoji.invoke(adapterPosition, it)
//            }

            //После регистрации проработать логику
            when (item.sender_email) {
                "kozlovdiman_je@yahoo.com" -> {
                    customViewGroup.setRectangleColor(R.color.teal_700)
                    customViewGroup.setYoursMessage(true)
                    avatarImageView.isVisible = false
                    messageTitleTextView.layoutParams.height = 0
                    messageTitleTextView.text = ""
                }
                else -> {
                    customViewGroup.setRectangleColor(R.color.unselected)
                    customViewGroup.setYoursMessage(false)
                    avatarImageView.isVisible = true
                    messageTitleTextView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
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
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.time_tv,
                        parent,
                        false
                    )
                DataHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val inform = differ.currentList[position]) {
            is SelectViewTypeClass.Chat.Date -> (holder as MessageAdapter.DataHolder).bind(inform.time)
            is SelectViewTypeClass.Chat.Message -> {
                (holder as MessageAdapter.MessageHolder).bind(inform)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateChatList(chatList: List<SelectViewTypeClass.Chat>) {
        differ.submitList(chatList)
    }
}
