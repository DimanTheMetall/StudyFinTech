package com.example.homework2.mvp.chat

import android.util.Log
import com.example.homework2.Constance
import com.example.homework2.data.local.entity.MessageEntity
import com.example.homework2.dataclasses.chatdataclasses.EditMessage
import com.example.homework2.dataclasses.chatdataclasses.JsonMessages
import com.example.homework2.dataclasses.chatdataclasses.JsonResponse
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.JsonTopic
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.repositories.ChatRepositoryImpl
import com.example.homework2.repositories.StreamRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ChatModelImpl @Inject constructor(
    private val chatRepositoryImpl: ChatRepositoryImpl,
    private val streamsRepositoryImpl: StreamRepository
) : BaseModelImpl(), ChatModel {

    //HTTP operation

    override fun loadMessageById(messageId: Long): Single<SelectViewTypeClass.Message> {
        return chatRepositoryImpl.loadMessageById(messageId = messageId)
    }

    override fun deleteMessageFromDB(message: SelectViewTypeClass.Message): Completable {
        return chatRepositoryImpl.deleteMessageFromDB(message = message)
    }

    override fun deleteMessageFromZulip(messageId: Long): Single<JsonResponse> {
        return chatRepositoryImpl.deleteMessageFromZulip(messageId = messageId)
    }

    override fun deleteMessagesFromTopic(topic: Topic, stream: Stream): Completable {
        return chatRepositoryImpl.deleteMessagesFromTopic(stream = stream, topic = topic)
    }

    override fun loadTopicList(streamId: Int): Single<JsonTopic> {
        return streamsRepositoryImpl.getTopicList(streamId = streamId)
    }

    override fun editMessageInZulip(message: SelectViewTypeClass.Message): Single<JsonResponse> {
        return chatRepositoryImpl.editMessageInZulip(
            EditMessage(
                messageId = message.id,
                topic = message.subject,
                content = message.content
            )
        )
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
    ): Single<JsonResponse> {
        return chatRepositoryImpl.sendMessage(sentText = sentText, topic = topic, stream = stream)
    }

    override fun loadTopicMessages(
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

    override fun loadStreamMessages(
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<JsonMessages> {
        return chatRepositoryImpl.loadStreamMessages(
            stream = stream,
            anchor = anchor,
            numAfter = numAfter,
            numBefore = numBefore
        )
    }

    override fun loadLastMessage(
        topic: Topic,
        stream: Stream
    ): Single<List<SelectViewTypeClass.Message>> {
        return chatRepositoryImpl.loadLastMessageInTopic(topic = topic, stream = stream)
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

    override fun getMessageFromDataBase(
        stream: Stream,
        topic: Topic
    ): Single<MutableList<SelectViewTypeClass.Message>> {
        return chatRepositoryImpl.selectMessage(stream = stream, topic = topic)
    }

    override fun updateMessageInDB(message: SelectViewTypeClass.Message): Completable {
        return chatRepositoryImpl.updateMessageInDB(messageEntity = MessageEntity.toEntity(message))
    }

    override fun insertSingleMessageInDB(message: SelectViewTypeClass.Message): Completable {
        return chatRepositoryImpl.insertMessageInDB(messageEntity = MessageEntity.toEntity(message))
    }

}
