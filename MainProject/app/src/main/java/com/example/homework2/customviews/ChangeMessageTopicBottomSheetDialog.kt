package com.example.homework2.customviews

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework2.R
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.dpToPx
import com.example.homework2.mvp.chat.TopicsHelpAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChangeMessageTopicBottomSheetDialog(
    context: Context,
    private val onTopicCLick: (SelectViewTypeClass.Message) -> Unit
) :
    BottomSheetDialog(context) {

    private val topicAdapter = TopicsHelpAdapter { topic -> editMessage(topic = topic) }
    private lateinit var message: SelectViewTypeClass.Message

    private val itemDivider = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.apply {
                top += context.dpToPx(2)
                bottom += context.dpToPx(2)
            }
        }
    }

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
        findViewById<RecyclerView?>(R.id.topics_of_this_message_rc_view).apply {
            this?.adapter = topicAdapter
            this?.layoutManager = LinearLayoutManager(context)
            this?.addItemDecoration(itemDivider)
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
