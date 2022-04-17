package com.example.homework2.mvp.chat

import com.example.homework2.Constance
import com.example.homework2.data.ZulipDataBase
import com.example.homework2.data.local.entity.MessageEntity
import com.example.homework2.data.local.entity.ReactionEntity
import com.example.homework2.dataclasses.chatdataclasses.*
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChatModelImpl(
    private val database: ZulipDataBase,
    private val retrofitService: RetrofitService
) : BaseModelImpl(), ChatModel {


    //HTTP operation

    override fun loadMessageById(messageId: Long): Single<SelectViewTypeClass.Chat.Message> {
        return retrofitService.getOneMessage(messageId = messageId, false)
            .subscribeOn(Schedulers.io())
            .map { it.message }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<JsonRespone> {
        return retrofitService.deleteEmoji(messageId, emojiName, reactionType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun addEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<JsonRespone> {
        return retrofitService.addEmoji(messageId, emojiName, reactionType, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    override fun sendMessage(
        sentText: String,
        topic: Topic,
        stream: Stream
    ): Single<ResponseFromSendMessage> {
        val sentMessage = SendMessage(
            type = Constance.STREAM,
            to = stream.name,
            content = sentText,
            topic = topic.name
        )

        return retrofitService.sendMessage(
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
        lastMessageId: String,
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
            anchor = lastMessageId,
            numBefore = numBefore,
            numAfter = numAfter,
            false
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadLastMessage(
        topic: Topic,
        stream: Stream
    ): Single<List<SelectViewTypeClass.Chat.Message>> {
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

    override fun insertAllMessagesAndReactions(messages: List<SelectViewTypeClass.Chat.Message>) {
        val messagesDisposable = database.getMessagesAndReactionDao()
            .insertMessagesFromTopic(messages.map { MessageEntity.toEntity(it) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        messages.forEach { message ->
            val insertDisposableReaction =
                database.getMessagesAndReactionDao().insertAllReactionOnMessages(message.reactions
                    .map { reaction ->
                        ReactionEntity.toEntity(
                            reaction = reaction,
                            messageId = message.id
                        )
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            compositeDisposable.add(insertDisposableReaction)
        }
        compositeDisposable.add(messagesDisposable)
    }

    override fun deleteOldestMessagesWhereIdLess(
        messageIdToSave: Long,
        stream: Stream,
        topic: Topic
    ) {
        val disposable = database.getMessagesAndReactionDao()
            .deleteOldestMessages(
                oldestMessagedItAfterDeleted = messageIdToSave,
                streamId = stream.stream_id,
                topicName = topic.name
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        compositeDisposable.add(disposable)
    }

    override fun selectMessage(
        stream: Stream,
        topic: Topic
    ): Single<Map<MessageEntity, List<ReactionEntity>>> {
        return database.getMessagesAndReactionDao()
            .selectMessagesAndReactionFromTopic(topicName = topic.name, streamId = stream.stream_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}