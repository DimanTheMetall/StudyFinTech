package com.example.homework2.mvp.chat

import android.util.Log
import com.example.homework2.Constance
import com.example.homework2.data.local.entity.MessageEntity
import com.example.homework2.data.local.entity.ReactionEntity
import com.example.homework2.dataclasses.chatdataclasses.JsonMessages
import com.example.homework2.dataclasses.chatdataclasses.JsonResponse
import com.example.homework2.dataclasses.chatdataclasses.ResponseFromSendMessage
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.repositories.ChatRepository
import io.reactivex.Single
import javax.inject.Inject

class ChatModelImpl @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseModelImpl(), ChatModel {

    //HTTP operation

    override fun getMessageById(messageId: Long): Single<SelectViewTypeClass.Message> {
        return chatRepository.loadMessageById(messageId = messageId)
    }

    override fun deleteEmoji(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ): Single<JsonResponse> {
        return chatRepository.deleteEmoji(
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
        return chatRepository.addEmoji(
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
        return chatRepository.sendMessage(sentText = sentText, topic = topic, stream = stream)
    }

    override fun getTopicMessages(
        topic: Topic,
        stream: Stream,
        anchor: String,
        numAfter: Int,
        numBefore: Int
    ): Single<JsonMessages> {
        return chatRepository.loadTopicMessages(
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
        return getLastMessage(topic = topic, stream = stream)
    }

    //Database operation

    override fun insertAllMessagesAndReactions(messages: List<SelectViewTypeClass.Message>) {
        val disposable = chatRepository.insertAllMessagesAndReactions(messages = messages)
            .subscribe(
                {
                    Log.d(
                        Constance.LogTag.MESSAGES_AND_REACTIONS,
                        "INSERT MESSAGE AND REACTION COMPLETE"
                    )
                },
                {
                    Log.e(
                        Constance.LogTag.MESSAGES_AND_REACTIONS,
                        "INSERT MESSAGE AND REACTION FAILED",
                        it
                    )
                })

        compositeDisposable.add(disposable)
    }

    override fun deleteOldestMessagesWhereIdLess(
        messageIdToSave: Long,
        stream: Stream,
        topic: Topic
    ) {
        val disposable = chatRepository.deleteOldestMessagesWhereIdLess(
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
    ): Single<Map<MessageEntity, List<ReactionEntity>>> {
        return chatRepository.selectMessage(stream = stream, topic = topic)
    }
}
