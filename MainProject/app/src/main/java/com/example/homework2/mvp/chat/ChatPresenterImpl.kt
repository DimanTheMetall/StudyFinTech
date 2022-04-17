package com.example.homework2.mvp.chat

import com.example.homework2.Constance
import com.example.homework2.dataclasses.Stream
import com.example.homework2.dataclasses.Topic
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.mvp.BasePresenterImpl

class ChatPresenterImpl(
    view: ChatView,
    model: ChatModel
) : BasePresenterImpl<ChatView, ChatModel>(view, model), ChatPresenter {

    private var loadedIsLast = false
    private val currentMessageList = mutableListOf<SelectViewTypeClass.Chat.Message>()

    override fun checkAndDelete(stream: Stream, topic: Topic) {
        if (currentMessageList.size > 50) {
            model.deleteOldestMessagesWhereIdLess(
                messageIdToSave = currentMessageList[currentMessageList.lastIndex - 50].id,
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
        if (isSelected) {
            model.deleteEmoji(
                messageId = messageId,
                emojiName = emojiName,
                reactionType = reactionType
            )
            val deleteDisposable = model.loadMessageById(messageId = messageId)
                .subscribe({ message ->
                    currentMessageList.map { if (it.id == message.id) message else it }
//                    view.replaceMessage(message)
                }, {})
            compositeDisposable.add(deleteDisposable)
        } else {
            model.addEmoji(
                messageId = messageId,
                emojiName = emojiName,
                reactionType = reactionType
            )
            val addDisposable = model.loadMessageById(messageId = messageId)
                .subscribe({ message ->
                    currentMessageList.map { if (it.id == message.id) message else it }
//                    view.replaceMessage(message = message)
                }, {})
            compositeDisposable.add(addDisposable)
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
                .subscribe({ json ->
                    val messages =
                        json.messages.filterNot { it.id == lastMessageId }
                    loadedIsLast = json.foundNewest
                    currentMessageList.addAll(messages)
                    view.showMessages(currentMessageList)
                    model.insertAllMessagesAndReactions(messages = messages)
                    checkAndDelete(stream = stream, topic = topic)
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
            }, {})
        if (resultMessages.isNullOrEmpty()) {
            onMessagesLoadRequested(stream = stream, topic = topic)
        } else {
            view.showMessages(resultMessages)
        }
        compositeDisposable.add(disposable)
    }

    override fun onInit() {

    }
}