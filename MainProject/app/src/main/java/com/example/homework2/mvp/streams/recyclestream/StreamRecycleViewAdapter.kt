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

class StreamRecycleViewAdapter(
    val openFrag: (Topic, Stream) -> Unit,
    val openDialog: () -> Unit,
    val onLongTap: (Stream) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(this, StreamsDiffCallback())

    private enum class SelectTypeStream {
        STREAM, CREATESTREAM
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (SelectTypeStream.values()[viewType]) {
            SelectTypeStream.STREAM -> {
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
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.create_channel_item, null, false).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }

                return LastStreamHolder(view)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == differ.currentList.lastIndex) SelectTypeStream.CREATESTREAM.ordinal
        else SelectTypeStream.STREAM.ordinal
    }

    inner class LastStreamHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.setOnClickListener {
                openDialog.invoke()
            }
        }
    }

    inner class ChannelHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = ChannelItemBinding.bind(item)
        var index = 0

        @SuppressLint("InflateParams")
        fun bind(stream: Stream) {

            binding.channelName.text = itemView.context.getString(R.string.item_grille, stream.name)
            binding.topicList.removeAllViews()
            openStreamTopics(false)
            setTopicList(stream = stream, topicList = stream.topicList, binding = binding)

            itemView.setOnLongClickListener {
                onLongTap.invoke(stream)
                true
            }

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
        if (holder is ChannelHolder) {
            holder.bind(differ.currentList[position])
        } else {
            (holder as LastStreamHolder).bind()
        }

    }

    fun updateList(list: List<Stream>) {
        val lastStreamForCreate = Stream(streamId = -1, name = "createStreams")
        val newList = list.sortedBy { it.name }.toMutableList()
        newList.add(lastStreamForCreate)

        differ.submitList(newList)
    }
}
