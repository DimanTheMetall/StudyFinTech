package com.example.homework2.mvp.streams.recyclestream

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.R
import com.example.homework2.StreamsDiffCallback
import com.example.homework2.databinding.ChannelItemBinding
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic

class StreamRecycleViewAdapter(val openFrag: (Topic, Stream) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //    private var streamList: List<Stream> = mutableListOf()
    private val differ = AsyncListDiffer(this, StreamsDiffCallback())

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.channel_item,
            null,
            false
        ).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        return ChannelHolder(view)
    }

    inner class ChannelHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = ChannelItemBinding.bind(item)
        var index = 0

        @SuppressLint("InflateParams")
        fun bind(stream: Stream) {

            binding.channelName.text = stream.name
            binding.topicList.removeAllViews()
            openStreamTopics(false)
            setTopicList(stream = stream, topicList = stream.topicList, binding = binding)

            itemView.setOnClickListener {
                when (binding.topicList.visibility) {
                    View.VISIBLE -> {
                        openStreamTopics(false)
                    }
                    else -> {
                        openStreamTopics(true)
                    }
                }
            }
        }

        private fun setTopicList(
            topicList: List<Topic>,
            binding: ChannelItemBinding,
            stream: Stream
        ) {
            topicList.forEach { topic ->
                if (index == 3) index = 0

                val view = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.streams_topic, null, false)
                    .apply {
                        when (index) {
                            0 -> setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.first_stream
                                )
                            )
                            1 -> setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.second_stream
                                )
                            )
                            2 -> setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.third_stream
                                )
                            )
                        }
                        index++

                        findViewById<TextView>(R.id.stream_name).text = topic.name

                        setOnClickListener {
                            openFrag.invoke(topic, stream)
                        }
                    }
                binding.topicList.addView(view)
            }
        }

        private fun openStreamTopics(isOpen: Boolean) {
            if (isOpen) {
                binding.topicList.visibility = View.VISIBLE
                binding.iconOpenStreams.rotation = 0f
            } else {
                binding.topicList.visibility = View.GONE
                binding.iconOpenStreams.rotation = 180f
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChannelHolder).bind(differ.currentList[position])
    }

    fun updateList(list: List<Stream>) {
        differ.submitList(list.sortedBy { it.name })
    }
}
