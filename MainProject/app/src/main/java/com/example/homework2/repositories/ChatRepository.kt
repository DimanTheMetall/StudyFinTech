package com.example.homework2.repositories

import androidx.room.Transaction
import com.example.homework2.Constants
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
    ): Single<ResultResponse>

    fun addEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<ResultResponse>

    fun sendMessage(
        sentText: String,
        topic: Topic,
        stream: Stream
    ): Single<ResultResponse>

    fun loadTopicMessages(
        topic: Topic,
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<ResultMessages>

    fun loadStreamMessages(
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<ResultMessages>

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

    fun selectMessagesToTopic(
        stream: Stream,
        topic: Topic
    ): Single<MutableList<SelectViewTypeClass.Message>>

    fun selectMessagesToStream(stream: Stream): Single<MutableList<SelectViewTypeClass.Message>>

    fun deleteMessageFromDB(message: SelectViewTypeClass.Message): Completable

    fun deleteMessageFromZulip(messageId: Long): Single<ResultResponse>

    fun editMessageInZulip(editMessage: EditMessage): Single<ResultResponse>

    fun updateMessageInDB(messageEntity: MessageEntity): Completable

    fun insertMessageInDB(messageEntity: MessageEntity): Completable

    fun deleteMessagesFromTopic(stream: Stream, topic: Topic): Completable

    fun deleteMessagesFromStream(stream: Stream): Completable

    fun deleteAllReactionsByMessageId(messageId: Long): Completable

    fun insertAllReactionOnMessage(reactionsList: List<Reaction>, messageId: Long): Completable

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
    ): Single<ResultResponse> {
        return retrofitService.deleteEmoji(messageId, emojiName, reactionType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun addEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<ResultResponse> {
        return retrofitService.addEmoji(messageId, emojiName, reactionType, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    override fun sendMessage(
        sentText: String,
        topic: Topic,
        stream: Stream
    ): Single<ResultResponse> {
        val sentMessage = SendMessage(
            type = Constants.STREAM,
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
    ): Single<ResultMessages> {
        return retrofitService.getMessages(
            narrow = Narrow(
                listOf(
                    Filter(operator = Constants.STREAM, operand = stream.name),
                    Filter(operator = Constants.TOPIC, operand = topic.name),
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
    ): Single<ResultMessages> {
        return retrofitService.getMessages(
            narrow = Narrow(
                listOf(
                    Filter(operator = Constants.STREAM, operand = stream.name)
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
                    Filter(operator = Constants.STREAM, operand = stream.name),
                    Filter(operator = Constants.TOPIC, operand = topic.name),
                )
            ).toJson(),
            anchor = Constants.Anchors.NEWEST,
            numBefore = 1,
            numAfter = 1,
            false
        )
            .subscribeOn(Schedulers.io())
            .map { it.messages }
            .observeOn(AndroidSchedulers.mainThread())

    }

    //Database operation

    @Transaction
    override fun insertAllMessagesAndReactions(messages: List<SelectViewTypeClass.Message>): Completable {
        return database.getMessagesAndReactionDao()
            .insertOrReplaceMessagesFromTopic(messages.map { MessageEntity.toEntity(it) })
            .subscribeOn(Schedulers.io())
            .andThen(
                Observable.fromIterable(messages)
                    .flatMapCompletable { message ->
                        database.getMessagesAndReactionDao()
                            .insertOrReplaceAllReactionOnMessages(
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

    override fun selectMessagesToTopic(
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

    override fun selectMessagesToStream(stream: Stream): Single<MutableList<SelectViewTypeClass.Message>> {
        return database.getMessagesAndReactionDao()
            .selectMessagesAndReactionFromStream(streamId = stream.streamId)
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

    override fun deleteMessageFromZulip(messageId: Long): Single<ResultResponse> {
        return retrofitService.deleteMessageById(messageId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun editMessageInZulip(editMessage: EditMessage): Single<ResultResponse> {
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

    override fun deleteMessagesFromTopic(stream: Stream, topic: Topic): Completable {
        return database.getMessagesAndReactionDao()
            .deleteAllMessagesFromTopic(streamId = stream.streamId, topicName = topic.name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteMessagesFromStream(stream: Stream): Completable {
        return database.getMessagesAndReactionDao()
            .deleteAllMessagesFromStream(streamId = stream.streamId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteAllReactionsByMessageId(messageId: Long): Completable {
        return database.getMessagesAndReactionDao()
            .deleteAllReactionsFromMessages(messageId = messageId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun insertAllReactionOnMessage(
        reactionsList: List<Reaction>,
        messageId: Long
    ): Completable {
        return database.getMessagesAndReactionDao()
            .insertOrReplaceAllReactionOnMessages(reactionsList.map { reaction ->
                ReactionEntity.toEntity(
                    reaction = reaction,
                    messageId = messageId
                )
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}
