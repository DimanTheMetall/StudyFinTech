package com.example.homework2

import androidx.recyclerview.widget.DiffUtil
import com.example.homework2.dataclasses.streamsandtopics.Stream

class StreamsDiffCallback : DiffUtil.ItemCallback<Stream>() {

    override fun areItemsTheSame(oldItem: Stream, newItem: Stream): Boolean {
        return oldItem.streamId == newItem.streamId
    }

    override fun areContentsTheSame(oldItem: Stream, newItem: Stream): Boolean {
        return oldItem == newItem
    }

}
