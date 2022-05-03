package com.example.homework2.mvp.chat

import android.util.Log
import com.example.homework2.Constance
import com.example.homework2.dataclasses.chatdataclasses.JsonMessages
import com.example.homework2.dataclasses.chatdataclasses.JsonResponse
import com.example.homework2.dataclasses.chatdataclasses.ResponseFromSendMessage
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.repositories.ChatRepositoryImpl
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ChatModelImpl @Inject constructor(
    private val chatRepositoryImpl: ChatRepositoryImpl
) : BaseModelImpl(), ChatModel {

    //HTTP operation

    override fun getMessageById(messageId: Long): Single<SelectViewTypeClass.Message> {
        return chatRepositoryImpl.loadMessageById(messageId = messageId)
    }

    override fun deleteEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<JsonResponse> {
        return chatRepositoryImpl.deleteEmoji(
            messageId = messageId,
            emojiName = emojiName,
            reactionType = reactionType
        )
    }

    override fun addEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<JsonResponse> {
        return chatRepositoryImpl.addEmoji(
            messageId = messageId,
            emojiName = emojiName,
            reactionType = reactionType
        )

    }

    override fun sendMessage(
        sentText: String,
        topic: Topic,
        stream: Stream
    ): Single<ResponseFromSendMessage> {
        return chatRepositoryImpl.sendMessage(sentText = sentText, topic = topic, stream = stream)
    }

    override fun getTopicMessages(
        topic: Topic,
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<JsonMessages> {
        return chatRepositoryImpl.loadTopicMessages(
            topic = topic,
            stream = stream,
            anchor = anchor,
            numAfter = numAfter,
            numBefore = numBefore
        )
    }

    override fun getLastMessage(
        topic: Topic,
        stream: Stream
    ): Single<List<SelectViewTypeClass.Message>> {
        return chatRepositoryImpl.loadLastMessage(topic = topic, stream = stream)
    }

    //Database operation

    override fun insertAllMessagesAndReactions(messages: List<SelectViewTypeClass.Message>): Completable {
        return chatRepositoryImpl.insertAllMessagesAndReactions(messages = messages)
    }

    override fun deleteOldestMessagesWhereIdLess(
        messageIdToSave: Long,
        stream: Stream,
        topic: Topic
    ) {
        val disposable = chatRepositoryImpl.deleteOldestMessagesWhereIdLess(
            messageIdToSave = messageIdToSave,
            stream = stream, topic = topic
        )
            .subscribe({
                Log.d(
                    Constance.LogTag.MESSAGES_AND_REACTIONS,
                    "DELETE OLDEST MESSAGE SUCCESS"
                )
            }, {
                Log.e(Constance.LogTag.MESSAGES_AND_REACTIONS, "DELETE OLDEST MESSAGE FAILED", it)
            })
        compositeDisposable.add(disposable)
    }

    override fun getMessage(
        stream: Stream,
        topic: Topic
    ): Single<MutableList<SelectViewTypeClass.Message>> {
        return chatRepositoryImpl.selectMessage(stream = stream, topic = topic)
    }
}
