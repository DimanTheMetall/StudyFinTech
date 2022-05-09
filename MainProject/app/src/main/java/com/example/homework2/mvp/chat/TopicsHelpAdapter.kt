package com.example.homework2.mvp.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.R
import com.example.homework2.databinding.HelpTopicItemBinding
import com.example.homework2.dataclasses.streamsandtopics.Topic

class TopicsHelpAdapter(
    val onItemCLick: (Topic) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var topicsList = listOf<Topic>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.help_topic_item, null, false)
                .apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
        return TopicHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TopicHolder).bind(topicsList[position])
    }

    override fun getItemCount(): Int = topicsList.size

    private inner class TopicHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = HelpTopicItemBinding.bind(item)
        fun bind(topic: Topic) {
            binding.topicNameHelp.text = topic.name
            itemView.setOnClickListener { onItemCLick.invoke(topic) }
        }
    }

    fun setTopicsList(newList: List<Topic>) {
        topicsList = newList
        notifyDataSetChanged()
    }

}
