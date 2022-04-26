package com.example.homework2.mvp.chat

import com.example.homework2.Constance
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BasePresenterImpl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChatPresenterImpl @Inject constructor(
    model: ChatModel
) : BasePresenterImpl<ChatView, ChatModel>(model), ChatPresenter {

    private var loadedIsLast = false
    private var loadedIsFirst = false
    private var refresherIsSubscribed = false
    private var currentMessageList = mutableListOf<SelectViewTypeClass.Message>()
    private val refresherObsevable = Observable.interval(
        Constance.INIT_REFRESHER_DELAY,
        Constance.DOWNLOAD_MESSAGES_PERIOD,
        TimeUnit.SECONDS
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun checkAndDelete(stream: Stream, topic: Topic) {
        if (currentMessageList.size > Constance.LIMIT_MESSAGE_COUNT_FOR_TOPIC) {
            model.deleteOldestMessagesWhereIdLess(
                messageIdToSave =
                currentMessageList[currentMessageList.lastIndex - Constance.LIMIT_MESSAGE_COUNT_FOR_TOPIC].id,
                stream = stream,
                topic = topic
            )
        }
    }

    private fun loadNextMessages(stream: Stream, topic: Topic) {
        val lastMessageId =
            if (currentMessageList.isNullOrEmpty()) 1L else currentMessageList.last().id
        view.showProgress()
        @Suppress("UNCHECKED_CAST") val disposable = model.loadTopicMessages(
            topic = topic,
            stream = stream,
            anchor = "$lastMessageId",
            numAfter = Constance.MESSAGES_COUNT_PAGINATION,
            numBefore = 0
        )
            .subscribe(
                { json ->
                    val messages =
                        json.messages.filterNot { it.id == lastMessageId }

                    loadedIsLast = json.foundNewest
                    if (loadedIsLast) subscribeRefresher(topic = topic, stream = stream)

                    currentMessageList.addAll(messages)
                    view.showMessages(currentMessageList)

                    model.insertAllMessagesAndReactions(messages = messages)
                    checkAndDelete(stream = stream, topic = topic)
                },
                {
                    view.showError(it)
                })

        compositeDisposable.add(disposable)
    }

    private fun subscribeRefresher(topic: Topic, stream: Stream) {
        if (!refresherIsSubscribed) {
            val refreshDisposable = refresherObsevable
                .subscribe({ loadNextMessages(stream = stream, topic = topic) }, {})

            refresherIsSubscribed = true
            compositeDisposable.add(refreshDisposable)
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

    override fun onMessagesNextPageLoadRequested(stream: Stream, topic: Topic) {
        if (!loadedIsLast) {
            loadNextMessages(stream = stream, topic = topic)
        }
    }

    override fun onMessagePreviousPageLoadRequest(stream: Stream, topic: Topic) {
        if (!loadedIsFirst) {
            view.showProgress()
            val disposable = model.loadTopicMessages(
                topic = topic,
                stream = stream,
                anchor = currentMessageList.first().id.toString(),
                numAfter = 0,
                numBefore = Constance.MESSAGES_COUNT_PAGINATION
            )
                .subscribe({ json ->
                    val newList = json.messages
                        .filterNot { it.id == currentMessageList.first().id }.toMutableList()
                    newList.addAll(currentMessageList)
                    currentMessageList = newList
                    view.showMessages(currentMessageList)
                }, { view.showError(it) })

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
            }, { view.showError(it) })
        compositeDisposable.add(sendDisposable)
    }

    override fun onInitMessageRequest(stream: Stream, topic: Topic) {
        val resultMessages = mutableListOf<SelectViewTypeClass.Message>()
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
                    loadNextMessages(stream = stream, topic = topic)
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
                    }, {
                        view.showError(it)
                    })
                compositeDisposable.add(messageDisposable)
            }, { view.showError(it) })
        compositeDisposable.add(emojiDisposable)
    }

    override fun onInit() {
        currentMessageList.clear()
        loadedIsLast = false
    }
}
