package com.example.homework2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework2.DiffCallback
import com.example.homework2.R
import com.example.homework2.customviews.CustomViewGroup
import com.example.homework2.databinding.CustomViewGroupLayoutBinding
import com.example.homework2.databinding.TimeTvBinding
import com.example.homework2.dataclasses.chatdataclasses.Reaction
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass

class MessageAdapter(
    val onTab: (Long) -> Unit,
    val onEmoji: (Reaction, Boolean, Long) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(this, DiffCallback())

    private enum class MessageType {
        MESSAGE, DATE
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is SelectViewTypeClass.Chat.Date -> MessageType.DATE.ordinal
            is SelectViewTypeClass.Chat.Message -> MessageType.MESSAGE.ordinal
        }
    }

    class DateHolder(view: View) : RecyclerView.ViewHolder(view) {
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

            //Кликер на "+"
            customFlexBox.getChildAt(customFlexBox.childCount - 1)
                .setOnClickListener {
                    onTab.invoke(item.id)
                }

            binding.root.setOnLongClickListener {
                onTab.invoke(item.id)
                true
            }

            customViewGroup.clearEmoji()

            val byEmojiName = item.reactions.groupBy { it.emoji_name }

            for (reactions in byEmojiName) {
                var emojiCount = 1
                var meIsAdded = false
                reactions.value.forEach {
                    meIsAdded = it.user_id == 490112
                }
                emojiCount = reactions.value.size


                customViewGroup.addEmoji(
                    reactions.value[0],
                    emojiCount, meIsAdded
                ) { reaction, isSelected ->
                    onEmoji.invoke(
                        reaction,
                        isSelected,
                        item.id
                    )
                }
            }

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
            MessageType.DATE -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.time_tv,
                        parent,
                        false
                    )
                DateHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (val inform = differ.currentList[position]) {
            is SelectViewTypeClass.Chat.Date -> (holder as DateHolder).bind(inform.time)
            is SelectViewTypeClass.Chat.Message -> {
                (holder as MessageHolder).bind(inform)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    //Разобраться с типизацией сообщений и времени
    fun addMessagesToList(messages: List<SelectViewTypeClass.Chat>) {
        val newList: MutableList<SelectViewTypeClass.Chat> = mutableListOf()
        newList.addAll(differ.currentList)
        newList.addAll(messages)
        newList.sortedBy { (it as SelectViewTypeClass.Chat.Message).id }
        differ.submitList(newList)
    }

}
