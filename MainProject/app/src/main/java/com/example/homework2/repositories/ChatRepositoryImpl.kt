package com.example.homework2.repositories

import com.example.homework2.Constance
import com.example.homework2.data.ZulipDataBase
import com.example.homework2.data.local.entity.MessageEntity
import com.example.homework2.data.local.entity.ReactionEntity
import com.example.homework2.dataclasses.chatdataclasses.*
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface ChatRepository {

    fun loadMessageById(messageId: Long): Single<SelectViewTypeClass.Message>

    fun deleteEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<JsonResponse>

    fun addEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<JsonResponse>

    fun sendMessage(
        sentText: String,
        topic: Topic,
        stream: Stream
    ): Single<JsonResponse>

    fun loadTopicMessages(
        topic: Topic,
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<JsonMessages>

    fun loadStreamMessages(
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<JsonMessages>

    fun loadLastMessageInTopic(
        topic: Topic,
        stream: Stream
    ): Single<List<SelectViewTypeClass.Message>>

    fun insertAllMessagesAndReactions(messages: List<SelectViewTypeClass.Message>): Completable

    fun deleteOldestMessagesWhereIdLess(
        messageIdToSave: Long,
        stream: Stream,
        topic: Topic
    ): Completable

    fun selectMessage(
        stream: Stream,
        topic: Topic
    ): Single<MutableList<SelectViewTypeClass.Message>>

    fun deleteMessageFromDB(message: SelectViewTypeClass.Message): Completable

    fun deleteMessageFromZulip(messageId: Long): Single<JsonResponse>

    fun editMessageInZulip(editMessage: EditMessage): Single<JsonResponse>

    fun updateMessageInDB(messageEntity: MessageEntity): Completable

    fun insertMessageInDB(messageEntity: MessageEntity): Completable
}

class ChatRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService,
    private val database: ZulipDataBase
) : ChatRepository {

//HTTP operation

    override fun loadMessageById(messageId: Long): Single<SelectViewTypeClass.Message> {
        return retrofitService.getOneMessage(messageId = messageId, false)
            .subscribeOn(Schedulers.io())
            .map { it.message }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<JsonResponse> {
        return retrofitService.deleteEmoji(messageId, emojiName, reactionType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun addEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<JsonResponse> {
        return retrofitService.addEmoji(messageId, emojiName, reactionType, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    override fun sendMessage(
        sentText: String,
        topic: Topic,
        stream: Stream
    ): Single<JsonResponse> {
        val sentMessage = SendMessage(
            type = Constance.STREAM,
            to = stream.name,
            content = sentText,
            topic = topic.name
        )

        return retrofitService.sendMessageInTopic(
            type = sentMessage.type,
            to = sentMessage.to,
            content = sentMessage.content,
            topic = sentMessage.topic
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadTopicMessages(
        topic: Topic,
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<JsonMessages> {
        return retrofitService.getMessages(
            narrow = Narrow(
                listOf(
                    Filter(operator = Constance.STREAM, operand = stream.name),
                    Filter(operator = Constance.TOPIC, operand = topic.name),
                )
            ).toJson(),
            anchor = anchor,
            numBefore = numBefore,
            numAfter = numAfter,
            false
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadStreamMessages(
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<JsonMessages> {
        return retrofitService.getMessages(
            narrow = Narrow(
                listOf(
                    Filter(operator = Constance.STREAM, operand = stream.name)
                )
            ).toJson(),
            anchor = anchor,
            numBefore = numBefore,
            numAfter = numAfter,
            false
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadLastMessageInTopic(
        topic: Topic,
        stream: Stream
    ): Single<List<SelectViewTypeClass.Message>> {
        return retrofitService.getMessages(
            narrow = Narrow(
                listOf(
                    Filter(operator = Constance.STREAM, operand = stream.name),
                    Filter(operator = Constance.TOPIC, operand = topic.name),
                )
            ).toJson(),
            anchor = Constance.Anchors.NEWEST,
            numBefore = 1,
            numAfter = 1,
            false
        )
            .subscribeOn(Schedulers.io())
            .map { it.messages }
            .observeOn(AndroidSchedulers.mainThread())

    }

    //Database operation

    override fun insertAllMessagesAndReactions(messages: List<SelectViewTypeClass.Message>): Completable {
        return database.getMessagesAndReactionDao()
            .insertMessagesFromTopic(messages.map { MessageEntity.toEntity(it) })
            .subscribeOn(Schedulers.io())
            .andThen(
                Observable.fromIterable(messages)
                    .flatMapCompletable { message ->
                        database.getMessagesAndReactionDao()
                            .insertAllReactionOnMessages(
                                message.reactions
                                    .map { reaction ->
                                        ReactionEntity.toEntity(
                                            reaction = reaction,
                                            messageId = message.id
                                        )
                                    }
                            )
                    }
            )
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteOldestMessagesWhereIdLess(
        messageIdToSave: Long,
        stream: Stream,
        topic: Topic
    ): Completable {
        return database.getMessagesAndReactionDao()
            .deleteOldestMessages(
                oldestMessagedItAfterDeleted = messageIdToSave,
                streamId = stream.streamId,
                topicName = topic.name
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    }

    override fun selectMessage(
        stream: Stream,
        topic: Topic
    ): Single<MutableList<SelectViewTypeClass.Message>> {
        return database.getMessagesAndReactionDao()
            .selectMessagesAndReactionFromTopic(topicName = topic.name, streamId = stream.streamId)
            .subscribeOn(Schedulers.io())
            .map { map ->
                val resultMessages = mutableListOf<SelectViewTypeClass.Message>()
                map.keys.forEach { messageEntity ->
                    val message = messageEntity.toMessage()
                    val reactionList =
                        map.getValue(messageEntity).map { it.toReaction() }
                    message.reactions = reactionList
                    resultMessages.add(message)
                }
                resultMessages
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteMessageFromDB(message: SelectViewTypeClass.Message): Completable {
        return database.getMessagesAndReactionDao()
            .deleteMessage(MessageEntity.toEntity(message = message))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteMessageFromZulip(messageId: Long): Single<JsonResponse> {
        return retrofitService.deleteMessageById(messageId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun editMessageInZulip(editMessage: EditMessage): Single<JsonResponse> {
        return retrofitService.editMessage(
            msg_id = editMessage.messageId,
            topic = editMessage.topic,
            content = editMessage.content
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateMessageInDB(messageEntity: MessageEntity): Completable {
        return database.getMessagesAndReactionDao().updateMessage(messageEntity = messageEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun insertMessageInDB(messageEntity: MessageEntity): Completable {
        return database.getMessagesAndReactionDao().insertMessage(messageEntity = messageEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}
