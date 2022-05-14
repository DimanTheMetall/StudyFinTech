package com.example.homework2.customviews

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.R
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.chat.TopicsHelpAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChangeMessageTopicBottomSheetDialog(
    context: Context,
    private val onTopicCLick: (SelectViewTypeClass.Message) -> Unit
) :
    BottomSheetDialog(context) {

    private val topicAdapter = TopicsHelpAdapter { topic -> editMessage(topic = topic) }
    private lateinit var message: SelectViewTypeClass.Message

    init {
        setContentView(R.layout.change_topic_layout)
        initAdapter()
    }

    fun setTopicList(list: List<Topic>) {
        topicAdapter.setTopicsList(newList = list)
    }

    fun setMessage(message_: SelectViewTypeClass.Message) {
        message = message_
        findViewById<TextView?>(R.id.message_topic)?.apply {
            text = message_.subject
        }
    }

    private fun initAdapter() {
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.shape_divider)!!)

        findViewById<RecyclerView?>(R.id.topics_of_this_message_rc_view).apply {
            this?.adapter = topicAdapter
            this?.layoutManager = LinearLayoutManager(context)
            this?.addItemDecoration(divider)
        }
    }

    private fun editMessage(topic: Topic) {
        if (topic.name == message.subject) {
            Toast.makeText(
                context,
                context.getString(R.string.message_already_in_this_topic),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            onTopicCLick.invoke(message.copy(subject = topic.name))
        }
    }

}
