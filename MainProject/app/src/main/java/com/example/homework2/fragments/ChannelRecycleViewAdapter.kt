package com.example.homework2.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.Channel
import com.example.homework2.R
import com.example.homework2.databinding.ChannelItemBinding


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
        var index = 0
        fun bind(channel: Channel) {
            binding.channelName.text = channel.name
            binding.topicList.removeAllViews()
            openStreamsTopic(false)

            channel.topicList.forEach {
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

                        findViewById<TextView>(R.id.stream_name).text = it.topicName
                        findViewById<TextView>(R.id.message_count).text =
                            "mess ${it.newMessageCount}"
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
                        openStreamsTopic(false)
                    }
                    else -> {
                        openStreamsTopic(true)
                    }
                }
            }
        }
        private fun openStreamsTopic(isOpen: Boolean) {
            if (isOpen){
                binding.topicList.visibility = View.VISIBLE
                binding.iconOpenStreams.rotation = 0f
            } else {
                binding.topicList.visibility = View.GONE
                binding.iconOpenStreams.rotation = 180f
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
