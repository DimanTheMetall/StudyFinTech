package com.example.homework2.mvp.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework2.ChatDiffCallback
import com.example.homework2.Constants
import com.example.homework2.R
import com.example.homework2.customviews.CustomViewGroup
import com.example.homework2.databinding.CustomViewGroupLayoutBinding
import com.example.homework2.databinding.TimeTvBinding
import com.example.homework2.dataclasses.chatdataclasses.Reaction
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import org.joda.time.Instant
import org.joda.time.format.DateTimeFormat

class MessageAdapter(
    val onMessageLongTab: (SelectViewTypeClass.Message) -> Unit,
    val onEmoji: (Reaction, Boolean, Long) -> Unit,
    val onAddCLick: (SelectViewTypeClass.Message) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(this, ChatDiffCallback())

    private enum class MessageType {
        MESSAGE, DATE
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is SelectViewTypeClass.Date -> MessageType.DATE.ordinal
            is SelectViewTypeClass.Message -> MessageType.MESSAGE.ordinal
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

        fun bind(item: SelectViewTypeClass.Message) = with(binding) {
            messageTextView.text = item.content
            messageTitleTextView.text = item.senderFullName

            //Нулабельность из-за рефлексии ретрофита
            if (!item.avatarUrl.isNullOrEmpty()) {
                Glide.with(customViewGroup.context)
                    .load(item.avatarUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(binding.avatarImageView)
            } else {
                binding.avatarImageView.setImageResource(R.mipmap.ic_launcher)
            }

            //Кликер на "+"
            customFlexBox.getChildAt(customFlexBox.childCount - 1)
                .setOnClickListener {
                    onAddCLick.invoke(item)
                }

            binding.root.setOnLongClickListener {
                onMessageLongTab.invoke(item)
                true
            }

            customViewGroup.clearEmoji()

            val byEmojiName = item.reactions.groupBy { it.emojiName }

            for (reactions in byEmojiName) {
                var meIsAdded = false
                reactions.value.forEach {
                    meIsAdded = it.userId == Constants.myId
                }
                val emojiCount: Int = reactions.value.size


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
            when (item.senderEmail) {
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
            is SelectViewTypeClass.Date -> (holder as DateHolder).bind(inform.time)
            is SelectViewTypeClass.Message -> {
                (holder as MessageHolder).bind(inform)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun replaceMessageList(newList: List<SelectViewTypeClass.Message>) {
        val format = DateTimeFormat.forPattern("d MMMM, yyyy")
        val listWithTime: MutableList<SelectViewTypeClass> = mutableListOf()

        for (index in 0..newList.lastIndex) {
            val previousTime =
                Instant.ofEpochSecond(if (index == 0) 0L else newList[index - 1].timestamp)
                    .toDateTime().toString(format)

            val currentTime =
                Instant.ofEpochSecond(newList[index].timestamp).toDateTime().toString(format)

            if (previousTime != currentTime) {
                val dateView = SelectViewTypeClass.Date(currentTime)
                listWithTime.add(dateView)
            }
            listWithTime.add(newList[index])
        }

        differ.submitList(listWithTime)
    }
}
