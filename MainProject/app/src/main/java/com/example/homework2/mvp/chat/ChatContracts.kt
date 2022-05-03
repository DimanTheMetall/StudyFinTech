package com.example.homework2.mvp.chat

import com.example.homework2.Errors
import com.example.homework2.dataclasses.chatdataclasses.JsonMessages
import com.example.homework2.dataclasses.chatdataclasses.JsonResponse
import com.example.homework2.dataclasses.chatdataclasses.ResponseFromSendMessage
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Completable
import io.reactivex.Single

interface ChatView : BaseView {

    fun showError(throwable: Throwable, error: Errors)

    fun showProgress()

    fun showMessages(messages: List<SelectViewTypeClass.Message>)

}

interface ChatPresenter : BasePresenter {

    fun onEmojiInMessageClick(
        messageId: Long,
        emojiName: String,
        reactionType: String,
        isSelected: Boolean
    )

    fun onMessagesNextPageLoadRequested(stream: Stream, topic: Topic)

    fun onMessagePreviousPageLoadRequest(stream: Stream, topic: Topic)

    fun onSendMessageRequest(sentText: String, topic: Topic, stream: Stream)

    fun onInitMessageRequest(stream: Stream, topic: Topic)

    fun onEmojiInSheetDialogClick(messageId: Long, emojiName: String, reactionType: String)

}

interface ChatModel : BaseModel {

    fun getMessageById(messageId: Long): Single<SelectViewTypeClass.Message>

    fun deleteEmoji(messageId: Long, emojiName: String, reactionType: String): Single<JsonResponse>

    fun addEmoji(messageId: Long, emojiName: String, reactionType: String): Single<JsonResponse>

    fun sendMessage(sentText: String, topic: Topic, stream: Stream): Single<ResponseFromSendMessage>

    fun getTopicMessages(
        topic: Topic,
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<JsonMessages>

    fun getLastMessage(
        topic: Topic,
        stream: Stream
    ): Single<List<SelectViewTypeClass.Message>>

    fun insertAllMessagesAndReactions(messages: List<SelectViewTypeClass.Message>): Completable

    fun deleteOldestMessagesWhereIdLess(messageIdToSave: Long, stream: Stream, topic: Topic)

    fun getMessage(
        stream: Stream,
        topic: Topic
    ): Single<MutableList<SelectViewTypeClass.Message>>

}
