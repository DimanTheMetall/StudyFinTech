package com.example.homework2

import androidx.recyclerview.widget.DiffUtil
import com.example.homework2.dataclasses.SelectViewTypeClass

class DiffCallback : DiffUtil.ItemCallback<SelectViewTypeClass>() {
    override fun areItemsTheSame(
        oldItem: SelectViewTypeClass,
        newItem: SelectViewTypeClass
    ): Boolean {

        if ((oldItem is SelectViewTypeClass.Message) && (newItem is SelectViewTypeClass.Message)
        ) {
            return oldItem.id == newItem.id
        } else {
            if ((oldItem is SelectViewTypeClass.Date) && (newItem is SelectViewTypeClass.Date)) {
                return oldItem.time == newItem.time
            }
        }
        return false
    }

    override fun areContentsTheSame(
        oldItem: SelectViewTypeClass,
        newItem: SelectViewTypeClass
    ): Boolean {
        return oldItem == newItem
    }
}