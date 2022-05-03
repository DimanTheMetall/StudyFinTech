package com.example.homework2

import androidx.recyclerview.widget.DiffUtil
import com.example.homework2.dataclasses.streamsandtopics.Member

class PeoplesDiffCallback : DiffUtil.ItemCallback<Member>() {

    override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
        return newItem.userId == oldItem.userId
    }

    override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
        return newItem == oldItem
    }
}
