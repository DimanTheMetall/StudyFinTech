package com.example.homework2.mvp.chat

import android.util.Log
import com.example.homework2.Constance
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BasePresenterImpl

class ChatPresenterImpl(
    view: ChatView,
    model: ChatModel
) : BasePresenterImpl<ChatView, ChatModel>(view, model), ChatPresenter {

    private var loadedIsLast = false
    private val currentMessageList = mutableListOf<SelectViewTypeClass.Chat.Message>()

    override fun checkAndDelete(stream: Stream, topic: Topic) {
        if (currentMessageList.size > Constance.LIMIT_MESSAGE_COUNT_FOR_TOPIC) {
            model.deleteOldestMessagesWhereIdLess(
                messageIdToSave =
                currentMessageList[currentMessageList.lastIndex - Constance.LIMIT_MESSAGE_COUNT_FOR_TOPIC].id,
                stream = stream,
                topic = topic
            )
        }
    }

    override fun onEmojiInMessageClick(
        messageId: Long,
        emojiName: String,
        reactionType: String,
        isSelected: Boolean
    ) {
        if (!isSelected) {
            val disposable = model.deleteEmoji(
                messageId = messageId,
                emojiName = emojiName,
                reactionType = reactionType
            ).subscribe({
                val deleteDisposable = model.loadMessageById(messageId = messageId)
                    .subscribe({ message ->
                        val newList =
                            currentMessageList.map { oldMessage -> if (oldMessage.id == message.id) message else oldMessage }
                        view.showMessages(newList)
                    }, {})
                compositeDisposable.add(deleteDisposable)
            }, { })
            compositeDisposable.add(disposable)
        } else {
            val disposable = model.addEmoji(
                messageId = messageId,
                emojiName = emojiName,
                reactionType = reactionType
            )
                .subscribe({
                    val addDisposable = model.loadMessageById(messageId = messageId)
                        .subscribe({ message ->
                            val newList =
                                currentMessageList.map { oldMessage -> if (oldMessage.id == message.id) message else oldMessage }
                            view.showMessages(newList)
                        }, {})
                    compositeDisposable.add(addDisposable)
                }, {})
            compositeDisposable.add(disposable)
        }
    }

    override fun onMessagesLoadRequested(stream: Stream, topic: Topic) {
        if (!loadedIsLast) {
            val lastMessageId =
                if (currentMessageList.isNullOrEmpty()) 1L else currentMessageList.last().id
            view.showProgress()
            @Suppress("UNCHECKED_CAST") val disposable = model.loadTopicMessages(
                topic = topic,
                stream = stream,
                lastMessageId = "$lastMessageId",
                numAfter = Constance.MESSAGES_COUNT_PAGINATION,
                0
            )
                .subscribe(
                    { json ->
                        val messages =
                            json.messages.filterNot { it.id == lastMessageId }
                        loadedIsLast = json.foundNewest
                        currentMessageList.addAll(messages)
                        view.showMessages(currentMessageList)
                        try {
                            model.insertAllMessagesAndReactions(messages = messages)
                            checkAndDelete(stream = stream, topic = topic)
                        } catch (e: Exception) {
                            Log.e(Constance.Log.MESSAGES_AND_REACTIONS, e.toString())
                            view.showError()
                        }

                    },
                    {
                        view.showError()
                    })

            compositeDisposable.add(disposable)
        }
    }

    override fun onSendMessageRequest(sentText: String, topic: Topic, stream: Stream) {
        val sendDisposable = model.sendMessage(sentText = sentText, topic = topic, stream = stream)
            .subscribe({
                val loadDisposable = model.loadLastMessage(topic = topic, stream = stream)
                    .subscribe({ messages ->
                        currentMessageList.addAll(messages)
                        checkAndDelete(stream = stream, topic = topic)
                        view.showMessages(currentMessageList)
                    }, {})
                compositeDisposable.add(loadDisposable)
            }, { view.showError() })
        compositeDisposable.add(sendDisposable)
    }

    override fun onInitMessageRequest(stream: Stream, topic: Topic) {
        val resultMessages = mutableListOf<SelectViewTypeClass.Chat.Message>()
        val disposable = model.selectMessage(stream = stream, topic = topic)
            .subscribe({ map ->
                map.keys.forEach { messageEntity ->
                    val message = messageEntity.toMessage()
                    val reactionList =
                        map.getValue(messageEntity).map { it.toReaction() }
                    message.reactions = reactionList
                    resultMessages.add(message)
                }
                if (resultMessages.isNullOrEmpty()) {
                    onMessagesLoadRequested(stream = stream, topic = topic)
                } else {
                    currentMessageList.addAll(resultMessages)
                    view.showMessages(currentMessageList)
                }
            }, {})

        compositeDisposable.add(disposable)
    }

    override fun onEmojiInSheetDialogClick(
        messageId: Long,
        emojiName: String,
        reactionType: String
    ) {
        val emojiDisposable = model.addEmoji(
            messageId = messageId,
            emojiName = emojiName,
            reactionType = reactionType
        )
            .subscribe({
                val messageDisposable = model.loadMessageById(messageId = messageId)
                    .subscribe({ message ->
                        val newList =
                            currentMessageList.map { oldMessage -> if (oldMessage.id == message.id) message else oldMessage }
                        view.showMessages(newList)
                    }, {})
                compositeDisposable.add(messageDisposable)
            }, {})
        compositeDisposable.add(emojiDisposable)
    }

    override fun onInit() {
        currentMessageList.clear()
        loadedIsLast = false
    }
}
