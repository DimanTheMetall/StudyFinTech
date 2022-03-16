package com.example.homework2

import androidx.recyclerview.widget.DiffUtil
import com.example.homework2.customviews.SelectViewTypeClass

class DiffCallback : DiffUtil.ItemCallback<SelectViewTypeClass>() {
    override fun areItemsTheSame(
        oldItem: SelectViewTypeClass,
        newItem: SelectViewTypeClass
    ): Boolean {
        return (oldItem as SelectViewTypeClass.Message).emoji.last().userId ==
                (newItem as SelectViewTypeClass.Message).emoji.last().userId
    }

    override fun areContentsTheSame(
        oldItem: SelectViewTypeClass,
        newItem: SelectViewTypeClass
    ): Boolean {
        return oldItem == newItem
    }
}
