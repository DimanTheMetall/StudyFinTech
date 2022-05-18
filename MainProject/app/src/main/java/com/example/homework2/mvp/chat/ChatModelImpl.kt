package com.example.homework2.mvp.chat

import com.example.homework2.data.local.entity.MessageEntity
import com.example.homework2.dataclasses.chatdataclasses.*
import com.example.homework2.dataclasses.streamsandtopics.ResultTopic
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

    override fun deleteMessageFromZulip(messageId: Long): Single<ResultResponse> {
        return chatRepositoryImpl.deleteMessageFromZulip(messageId = messageId)
    }

    override fun deleteMessagesFromTopic(topic: Topic, stream: Stream): Completable {
        return chatRepositoryImpl.deleteMessagesFromTopic(stream = stream, topic = topic)
    }

    override fun deleteMessagesFromStream(stream: Stream): Completable {
        return chatRepositoryImpl.deleteMessagesFromStream(stream)
    }

    override fun loadTopicList(streamId: Int): Single<ResultTopic> {
        return streamsRepositoryImpl.getTopicList(streamId = streamId)
    }

    override fun editMessageInZulip(message: SelectViewTypeClass.Message): Single<ResultResponse> {
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
    ): Single<ResultResponse> {
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
    ): Single<ResultResponse> {
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
    ): Single<ResultResponse> {
        return chatRepositoryImpl.sendMessage(sentText = sentText, topic = topic, stream = stream)
    }

    override fun loadTopicMessages(
        topic: Topic,
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<ResultMessages> {
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
    ): Single<ResultMessages> {
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
    ): Completable {
        return chatRepositoryImpl.deleteOldestMessagesWhereIdLess(
            messageIdToSave = messageIdToSave,
            stream = stream, topic = topic
        )
    }

    override fun getTopicMessagesFromDB(
        stream: Stream,
        topic: Topic
    ): Single<MutableList<SelectViewTypeClass.Message>> {
        return chatRepositoryImpl.selectMessagesToTopic(stream = stream, topic = topic)
    }

    override fun getStreamMessagesFromDB(stream: Stream): Single<MutableList<SelectViewTypeClass.Message>> {
        return chatRepositoryImpl.selectMessagesToStream(stream = stream)
    }

    override fun updateMessageInDB(message: SelectViewTypeClass.Message): Completable {
        return chatRepositoryImpl.updateMessageInDB(messageEntity = MessageEntity.toEntity(message))
    }

    override fun insertSingleMessageInDB(message: SelectViewTypeClass.Message): Completable {
        return chatRepositoryImpl.insertMessageInDB(messageEntity = MessageEntity.toEntity(message))
    }

    override fun deleteTopicFromDB(stream: Stream, topic: Topic): Completable {
        return streamsRepositoryImpl.deleteTopic(stream = stream, topic = topic)
    }

    override fun insertTopicInDB(stream: Stream, topic: Topic): Completable {
        return streamsRepositoryImpl.insertTopic(stream = stream, topic = topic)
    }

    override fun deleteReactionsByMessageId(messageId: Long): Completable {
        return chatRepositoryImpl.deleteAllReactionsByMessageId(messageId = messageId)
    }

    override fun insertAllReactions(messageId: Long, reactionsList: List<Reaction>): Completable {
        return chatRepositoryImpl.insertAllReactionOnMessage(
            reactionsList = reactionsList,
            messageId = messageId
        )
    }

}
