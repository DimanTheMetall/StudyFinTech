package com.example.homework2.customviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.DiffCallback
import com.example.homework2.R
import com.example.homework2.databinding.CustomViewGroupLayoutBinding
import com.example.homework2.databinding.TimeTvBinding
import com.example.homework2.viewmodels.ChatViewModel


class MessageAdapter(val onTab: (Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(this, DiffCallback())
    private var currentId = 0


    init {
        for (index in differ.currentList.lastIndex downTo 0) {
            if (differ.currentList[index] is SelectViewTypeClass.Message) {
                currentId = (differ.currentList[index] as SelectViewTypeClass.Message).id
                continue
            }
        }

    }

    private enum class MessageType {
        MESSAGE, DATA
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is SelectViewTypeClass.Date -> MessageType.DATA.ordinal
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


            customFlexBox.getChildAt(customFlexBox.childCount - 1)
                .setOnClickListener {
                    onTab.invoke(adapterPosition)
                }

            customViewGroup.clearEmoji()

            item.emojiList.forEach {
                if (it.count != 0) customViewGroup.addEmoji(it)
            }

            binding.root.setOnLongClickListener {
                onTab.invoke(adapterPosition)
                true
            }

            when (item.isYou) {
                true -> {
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
                    LayoutInflater.from(parent.context).inflate(R.layout.time_tv, parent, false)
                MessageAdapter.DataHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val inform = differ.currentList[position]) {
            is SelectViewTypeClass.Date -> (holder as DataHolder).bind(inform.time)
            is SelectViewTypeClass.Message -> {
                (holder as MessageHolder).bind(inform)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateChatList(chatList: List<SelectViewTypeClass>) {
        differ.submitList(chatList)
    }

    fun getCurrentList(): List<SelectViewTypeClass> {
        return differ.currentList
    }

    fun addMessage(messageText: String, onAdd: (List<SelectViewTypeClass>) -> Unit) {
        val list = differ.currentList.toMutableList()
        list.add(
            SelectViewTypeClass.Message(
                currentId,
                messageText,
                "You Name",
                2,
                true
            )
        )
        differ.submitList(list)
        currentId++
        onAdd.invoke(list)
    }

    fun addEmojiReaction(reaction: Reaction, position: Int) {
        (differ.currentList[position] as? SelectViewTypeClass.Message)?.emojiList?.add(reaction)
        differ.submitList(differ.currentList)
        notifyItemChanged(position)
    }

    fun removeEmoji(position: Int) {
        (differ.currentList[position] as? SelectViewTypeClass.Message)?.emojiList?.removeLast()
        differ.submitList(differ.currentList)
    }
}
