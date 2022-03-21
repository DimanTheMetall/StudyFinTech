package com.example.homework2.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.Channel
import com.example.homework2.R
import com.example.homework2.databinding.ChannelItemBinding
import com.example.homework2.databinding.StreamsTopicBinding

class ChannelRecycleViewAdapter(val openFrag: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var channelList: MutableList<Channel> = mutableListOf()

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

        fun bind(channel: Channel) {
            binding.channelName.text = channel.name
            binding.topicList.removeAllViews()
            channel.topicList.forEach {
                val view = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.streams_topic, null, false)
                    .apply {
                        findViewById<TextView>(R.id.stream_name).text = it.topicName
                        findViewById<TextView>(R.id.message_count).text =
                            it.newMessageCount.toString()
                        setOnClickListener {
                            openFrag()
                            println("AAA topic clicked")
                        }
                    }
                binding.topicList.addView(view)
            }

            binding.iconOpenStreams.setOnClickListener {
                when (binding.topicList.visibility) {
                    View.VISIBLE -> {
                        binding.topicList.visibility = View.GONE
                        binding.iconOpenStreams.rotation = 180f
                    }
                    else -> {
                        binding.topicList.visibility = View.VISIBLE
                        binding.iconOpenStreams.rotation = 0f
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int = channelList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChannelRecycleViewAdapter.ChannelHolder).bind(channelList[position])
    }

    fun updateList(list: List<Channel>) {
        channelList = list.toMutableList()
        notifyDataSetChanged()
    }


}
