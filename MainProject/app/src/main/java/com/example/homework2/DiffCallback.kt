package com.example.homework2

import androidx.recyclerview.widget.DiffUtil
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass

class DiffCallback : DiffUtil.ItemCallback<SelectViewTypeClass.Chat>() {
    override fun areItemsTheSame(
        oldItem: SelectViewTypeClass.Chat,
        newItem: SelectViewTypeClass.Chat
    ): Boolean {

        if ((oldItem is SelectViewTypeClass.Chat.Message) && (newItem is SelectViewTypeClass.Chat.Message)
        ) {
            return oldItem.id == newItem.id
        } else {
            if ((oldItem is SelectViewTypeClass.Chat.Date) && (newItem is SelectViewTypeClass.Chat.Date)) {
                return oldItem.time == newItem.time
            }
        }
        return false
    }

    override fun areContentsTheSame(
        oldItem: SelectViewTypeClass.Chat,
        newItem: SelectViewTypeClass.Chat
    ): Boolean {
        return oldItem == newItem
    }
}
