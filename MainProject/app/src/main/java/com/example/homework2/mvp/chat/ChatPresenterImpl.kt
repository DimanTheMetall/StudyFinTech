package com.example.homework2.mvp.chat

import android.util.Log
import android.view.View
import com.example.homework2.Constants
import com.example.homework2.Errors
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BasePresenterImpl
import com.example.homework2.toErrorType
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
    private val refresherObservable = Observable.interval(
        Constants.INIT_REFRESHER_DELAY,
        Constants.DOWNLOAD_MESSAGES_PERIOD,
        TimeUnit.SECONDS
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

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
                        onReactionUpdateMapList(changedMessage = message)
                        view.showMessages(messages = currentMessageList)
                    }, {
                        view.showError(throwable = it, error = it.toErrorType())
                    })

                compositeDisposable.add(deleteDisposable)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

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
                            onReactionUpdateMapList(changedMessage = message)
                            view.showMessages(messages = currentMessageList)
                        }, {
                            view.showError(throwable = it, error = it.toErrorType())
                        })

                    compositeDisposable.add(addDisposable)
                }, {
                    view.showError(throwable = it, error = it.toErrorType())
                })

            compositeDisposable.add(disposable)
        }
    }

    override fun onDeleteMessageClick(message: SelectViewTypeClass.Message) {
        val deleteFromZulipDisposable = model.deleteMessageFromZulip(messageId = message.id)
            .subscribe({
                view.hideMessageBottomDialog()

                currentMessageList.remove(message)
                view.showMessages(currentMessageList)

                val deleteFromDBDisposable = model
                    .deleteMessageFromDB(message = message)
                    .subscribe({}, {
                        Log.e(
                            Constants.LogTag.MESSAGES_AND_REACTIONS,
                            "MESSAGE ${Constants.LogMessage.DELETE_ERROR}"
                        )
                    })

                compositeDisposable.add(deleteFromDBDisposable)
            }, { view.showError(throwable = it, error = it.toErrorType()) })
        compositeDisposable.add(deleteFromZulipDisposable)
    }

    override fun onAddReactionMessageClick(message: SelectViewTypeClass.Message) {
        view.hideMessageBottomDialog()
        view.showReactionDialog(message = message)
    }

    override fun onAddInMessageClick(message: SelectViewTypeClass.Message) {
        view.showReactionDialog(message = message)
    }

    override fun onMessageLongClick(message: SelectViewTypeClass.Message) {
        view.showMessageBottomDialog(message = message)
    }

    override fun onInTopicCLick(topic: Topic, stream: Stream) {
        view.openFrag(ChatFragment.newInstance(topic = topic, stream = stream))
    }

    override fun onTopicMessagesNextPageLoadRequested(stream: Stream, topic: Topic) {
        if (!loadedIsLast) loadNextTopicMessages(stream = stream, topic = topic)
    }

    override fun onTopicMessagePreviousPageLoadRequest(stream: Stream, topic: Topic) {
        if (!loadedIsFirst) loadPreviousTopicsMessages(stream = stream, topic = topic)
    }

    override fun onStreamMessageNextPageLoadRequest(stream: Stream) {
        if (!loadedIsLast) loadNextStreamMessages(stream = stream)
    }

    override fun onStreamMessagePreviousPgeLoadRequest(stream: Stream) {
        if (!loadedIsFirst) loadPreviousStreamMessages(stream = stream)
    }

    override fun onSendMessageInTopicRequest(
        sentText: String,
        topic: Topic,
        stream: Stream,
        isStreamChat: Boolean
    ) {
        val sendDisposable = model.sendMessage(sentText = sentText, topic = topic, stream = stream)
            .subscribe({
                val loadDisposable = model.loadLastMessage(topic = topic, stream = stream)
                    .subscribe({ messages ->
                        view.clearMessageField()
                        currentMessageList.addAll(messages)
                        if (!isStreamChat) {
                            checkAndDeleteTopicMessages(stream = stream, topic = topic)
                        } else {
                            updateHelpTopicList(streamId = stream.streamId)
                        }
                        view.showMessages(currentMessageList)
                    }, {
                        view.showError(throwable = it, error = it.toErrorType())
                    })

                compositeDisposable.add(loadDisposable)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(sendDisposable)
    }

    override fun onInitMessageForTopicRequest(stream: Stream, topic: Topic) {
        val disposable = model.getTopicMessagesFromDB(stream = stream, topic = topic)
            .subscribe({ messages ->
                if (messages.isNullOrEmpty()) {
                    loadNextTopicMessages(stream = stream, topic = topic)
                } else {
                    initRefreshLoadForTopicMessage(
                        stream = stream,
                        topic = topic,
                        messages = messages
                    )
                }
            }, {
                view.showError(throwable = it, error = Errors.SYSTEM)
            })

        compositeDisposable.add(disposable)
    }

    override fun onInitMessageForStreamRequest(stream: Stream) {
        val selectDisposable = model.getStreamMessagesFromDB(stream = stream)
            .subscribe({ messages ->
                if (messages.isEmpty()) {
                    loadNextStreamMessages(stream = stream)
                } else {
                    initRefreshLoadForStreamMessages(stream = stream, messages = messages)
                }
            }, {
                Log.e(
                    Constants.LogTag.MESSAGES_AND_REACTIONS,
                    "MESSAGES ${Constants.LogMessage.SELECT_ERROR}"
                )
            })
        compositeDisposable.add(selectDisposable)
    }

    override fun onEmojiInSheetDialogClick(
        message: SelectViewTypeClass.Message,
        emojiName: String,
        reactionType: String
    ) {
        var isExist = false
        message.reactions.forEach { reaction ->
            if (reaction.userId == Constants.myId && reaction.emojiName == emojiName) isExist = true
        }

        if (!isExist) {
            val emojiDisposable = model.addEmoji(
                messageId = message.id,
                emojiName = emojiName,
                reactionType = reactionType
            )
                .subscribe({
                    val messageDisposable = model.loadMessageById(messageId = message.id)
                        .subscribe({ message ->
                            refreshReactionOnMessage(message = message)
                            currentMessageList =
                                currentMessageList.map { oldMessage -> if (oldMessage.id == message.id) message else oldMessage }
                                    .toMutableList()
                            view.showMessages(messages = currentMessageList)
                        }, {
                            view.showError(throwable = it, error = it.toErrorType())
                        })

                    compositeDisposable.add(messageDisposable)
                }, {
                    view.showError(throwable = it, error = it.toErrorType())
                })

            compositeDisposable.add(emojiDisposable)
        }
    }

    override fun onHelpTopicItemCLick(topic: Topic) {
        view.changeHelpVisibility(visibility = View.INVISIBLE)
        view.fillTopicField(topic = topic)
    }

    override fun onCancelHelpBtnCLick() {
        view.changeHelpVisibility(View.GONE)
    }

    override fun onFocusChanged(isFocused: Boolean) {
        if (isFocused) {
            view.changeHelpVisibility(View.VISIBLE)
        } else {
            view.changeHelpVisibility(View.GONE)
        }
    }

    override fun onEditMessageClick(message: SelectViewTypeClass.Message) {
        view.showEditMessageDialog(message = message)
        view.hideMessageBottomDialog()
    }

    override fun onApplyEditMessageClick(
        message: SelectViewTypeClass.Message,
        isStreamChat: Boolean, stream: Stream
    ) {
        view.hideEditMessageDialog()
        view.hideChangeTopicDialog()
        view.showProgress()
        editMessage(
            message = message, isTopicChanged = false, isStreamChat = isStreamChat,
            stream = stream
        )
    }

    override fun onCancelEditMessageClick() {
        view.hideEditMessageDialog()
    }

    override fun onChangeTopicClick(message: SelectViewTypeClass.Message, stream: Stream) {
        view.hideMessageBottomDialog()
        view.showChangeTopicDialog(message = message, stream = stream)
    }

    override fun onApplyChangeTopicForMessage(
        message: SelectViewTypeClass.Message,
        isStreamChat: Boolean,
        stream: Stream
    ) {
        view.hideChangeTopicDialog()
        view.showProgress()
        editMessage(
            message = message, isTopicChanged = true, isStreamChat = isStreamChat,
            stream = stream
        )
    }

    override fun onCopyCLick() {
        view.hideMessageBottomDialog()
    }

    override fun onDestroy(isStreamChat: Boolean, stream: Stream, topic: Topic) {
        if (!isStreamChat && currentMessageList.isEmpty()) {
            val disposable = model.deleteTopicFromDB(stream = stream, topic = topic)
                .subscribe({}, {
                    Log.e(
                        Constants.LogTag.TOPIC_AND_STREAM,
                        "TOPIC ${Constants.LogMessage.DELETE_ERROR}"
                    )
                })
            compositeDisposable.add(disposable)
        }
    }

    override fun onInit() {
        currentMessageList.clear()
        loadedIsLast = false
    }

    private fun updateHelpTopicList(streamId: Int) {
        val disposable = model.loadTopicList(streamId = streamId)
            .subscribe({
                view.setTopicListInStream(topicList = it.topics)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(disposable)
    }

    private fun onReactionUpdateMapList(changedMessage: SelectViewTypeClass.Message) {
        currentMessageList =
            currentMessageList.map { oldMessage -> if (oldMessage.id == changedMessage.id) changedMessage else oldMessage }
                .toMutableList()
    }

    private fun loadPreviousStreamMessages(stream: Stream) {
        if (!loadedIsFirst) {
            view.showProgress()
            val disposable = model.loadStreamMessages(
                stream = stream,
                anchor = currentMessageList.first().id.toString(),
                numAfter = 0,
                numBefore = Constants.MESSAGES_COUNT_PAGINATION
            )
                .subscribe({ json ->
                    val newList = json.messages
                        .filterNot { it.id == currentMessageList.first().id }.toMutableList()
                    newList.addAll(currentMessageList)
                    currentMessageList = newList
                    view.showMessages(messages = currentMessageList)
                }, {
                    view.showError(throwable = it, error = it.toErrorType())
                })

            compositeDisposable.add(disposable)
        }
    }

    private fun loadNextStreamMessages(stream: Stream) {
        val lastMessageId =
            if (currentMessageList.isEmpty()) 1L else currentMessageList.last().id
        view.showProgress()
        val disposable = model.loadStreamMessages(
            stream = stream,
            anchor = lastMessageId.toString(),
            numAfter = Constants.MESSAGES_COUNT_PAGINATION,
            numBefore = 0
        )
            .subscribe({ resultMessages ->
                val messages = resultMessages.messages.filterNot { it.id == lastMessageId }
                loadedIsLast = resultMessages.foundNewest
                if (loadedIsLast) subscribeStreamsRefresher(stream = stream)
                val insertDisposable = model.insertAllMessagesAndReactions(messages)
                    .subscribe({
                        checkAndDeleteForStreamMessages(stream = stream)
                    }, {
                        Log.e(
                            Constants.LogTag.MESSAGES_AND_REACTIONS,
                            "MESSAGE ${Constants.LogMessage.INSERT_ERROR}"
                        )
                    })

                currentMessageList.addAll(messages)
                view.showMessages(messages = currentMessageList)

                compositeDisposable.add(insertDisposable)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(disposable)
    }


    private fun checkAndDeleteForStreamMessages(stream: Stream) {
        val bySubject = currentMessageList.groupBy { it.subject }
        //Subject ?????????????????? ?????? ?????? ????????????
        bySubject.keys.forEach { key ->
            val listOfTopicMessages = bySubject[key]
            if (listOfTopicMessages != null && listOfTopicMessages.size > Constants.LIMIT_MESSAGE_COUNT_FOR_TOPIC) {
                val deleteDisposable =
                    model.deleteOldestMessagesWhereIdLess(
                        messageIdToSave = listOfTopicMessages[listOfTopicMessages.lastIndex - Constants.LIMIT_MESSAGE_COUNT_FOR_TOPIC].id,
                        stream = stream,
                        topic = Topic(name = key)
                    )
                        .subscribe({ }, {
                            Log.e(
                                Constants.LogTag.MESSAGES_AND_REACTIONS,
                                "MESSAGES ${Constants.LogMessage.DELETE_ERROR}"
                            )
                        })

                compositeDisposable.add(deleteDisposable)
            }
        }
    }

    private fun checkAndDeleteTopicMessages(stream: Stream, topic: Topic) {
        if (currentMessageList.size > Constants.LIMIT_MESSAGE_COUNT_FOR_TOPIC) {
            val deleteDisposable = model.deleteOldestMessagesWhereIdLess(
                messageIdToSave =
                currentMessageList[currentMessageList.lastIndex - Constants.LIMIT_MESSAGE_COUNT_FOR_TOPIC].id,
                stream = stream,
                topic = topic
            )
                .subscribe({ }, {
                    Log.e(
                        Constants.LogTag.MESSAGES_AND_REACTIONS,
                        "MESSAGE ${Constants.LogMessage.DELETE_ERROR}"
                    )
                })

            compositeDisposable.add(deleteDisposable)
        }
    }

    private fun loadNextTopicMessages(stream: Stream, topic: Topic) {
        val lastMessageId =
            if (currentMessageList.isEmpty()) 1L else currentMessageList.last().id
        view.showProgress()
        @Suppress("UNCHECKED_CAST") val disposable = model.loadTopicMessages(
            topic = topic,
            stream = stream,
            anchor = "$lastMessageId",
            numAfter = Constants.MESSAGES_COUNT_PAGINATION,
            numBefore = 0
        )
            .subscribe(
                { json ->
                    val messages =
                        json.messages.filterNot { it.id == lastMessageId }

                    loadedIsLast = json.foundNewest
                    if (loadedIsLast) subscribeTopicRefresher(topic = topic, stream = stream)

                    currentMessageList.addAll(messages)
                    view.showMessages(currentMessageList)

                    val insertDisposable = model.insertAllMessagesAndReactions(messages = messages)
                        .subscribe(
                            {
                                checkAndDeleteTopicMessages(stream = stream, topic = topic)
                            }, {
                                Log.e(
                                    Constants.LogTag.MESSAGES_AND_REACTIONS,
                                    "MESSAGE ${Constants.LogMessage.INSERT_ERROR}"
                                )
                            })

                    compositeDisposable.add(insertDisposable)
                },
                {
                    view.showError(throwable = it, error = it.toErrorType())
                })

        compositeDisposable.add(disposable)
    }

    private fun loadPreviousTopicsMessages(stream: Stream, topic: Topic) {
        view.showProgress()
        val disposable = model.loadTopicMessages(
            topic = topic,
            stream = stream,
            anchor = currentMessageList.first().id.toString(),
            numAfter = 0,
            numBefore = Constants.MESSAGES_COUNT_PAGINATION
        )
            .subscribe({ json ->
                val newList = json.messages
                    .filterNot { it.id == currentMessageList.first().id }.toMutableList()
                newList.addAll(currentMessageList)
                currentMessageList = newList

                view.showMessages(messages = currentMessageList)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(disposable)
    }

    private fun subscribeTopicRefresher(topic: Topic, stream: Stream) {
        if (!refresherIsSubscribed) {
            val refreshDisposable = refresherObservable
                .subscribe({
                    loadNextTopicMessages(stream = stream, topic = topic)
                }, {
                    view.showError(throwable = it, error = it.toErrorType())
                })
            refresherIsSubscribed = true

            compositeDisposable.add(refreshDisposable)
        }
    }

    private fun subscribeStreamsRefresher(stream: Stream) {
        if (!refresherIsSubscribed) {
            val refreshDisposable = refresherObservable
                .subscribe({ loadNextStreamMessages(stream = stream) }, {
                    view.showError(throwable = it, error = it.toErrorType())
                })

            refresherIsSubscribed = true

            compositeDisposable.add(refreshDisposable)
        }
    }

    private fun editMessageInList(newMessage: SelectViewTypeClass.Message) {
        currentMessageList = currentMessageList.map { oldMessage ->
            if (newMessage.id != oldMessage.id) oldMessage else oldMessage.copy(
                content = newMessage.content
            )
        }.toMutableList()
    }

    private fun changeTopicInMessage(message: SelectViewTypeClass.Message, isStreamChat: Boolean) {
        var messageToChange: SelectViewTypeClass.Message? = null
        if (!isStreamChat) {
            currentMessageList.forEach { if (it.id == message.id) messageToChange = it }
            currentMessageList.remove(messageToChange)
        } else {
            currentMessageList.map { if (it.id == message.id) it.copy(subject = message.subject) else it }
        }
    }

    private fun updateMessageInDB(message: SelectViewTypeClass.Message) {
        val updateDisposable = model.updateMessageInDB(message)
            .subscribe({ }, {
                Log.e(
                    Constants.LogTag.MESSAGES_AND_REACTIONS,
                    "MESSAGE ${Constants.LogMessage.UPDATE_ERROR}"
                )
            })

        compositeDisposable.add(updateDisposable)
    }

    private fun editMessage(
        message: SelectViewTypeClass.Message,
        isTopicChanged: Boolean,
        isStreamChat: Boolean,
        stream: Stream
    ) {
        val editDisposable = model.editMessageInZulip(message = message)
            .subscribe({
                updateMessageInDB(message = message)
                if (isTopicChanged) {
                    changeTopicInMessage(
                        message = message,
                        isStreamChat = isStreamChat
                    )
                    val disposable = model.insertTopicInDB(stream, Topic(message.subject))
                        .subscribe({}, {
                            Log.e(
                                Constants.LogTag.MESSAGES_AND_REACTIONS,
                                "TOPIC ${Constants.LogMessage.INSERT_ERROR}"
                            )
                        })

                    compositeDisposable.add(disposable)
                } else editMessageInList(newMessage = message)

                view.showMessages(currentMessageList)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(editDisposable)
    }

    private fun initRefreshLoadForTopicMessage(
        stream: Stream,
        topic: Topic,
        messages: List<SelectViewTypeClass.Message>
    ) {
        view.showMessages(messages = messages)
        @Suppress("UNCHECKED_CAST") val disposable = model.loadTopicMessages(
            topic = topic,
            stream = stream,
            anchor = "${messages.first().id}",
            numAfter = Constants.LIMIT_MESSAGE_COUNT_FOR_TOPIC,
            numBefore = 0
        )
            .subscribe(
                { result ->
                    val newMessageList = result.messages
                    val deleteDisposable =
                        model.deleteMessagesFromTopic(stream = stream, topic = topic)
                            .subscribe({
                                val insertDisposable =
                                    model.insertAllMessagesAndReactions(messages = newMessageList)
                                        .subscribe({ }, {
                                            Log.e(
                                                Constants.LogTag.MESSAGES_AND_REACTIONS,
                                                "MESSAGE ${Constants.LogMessage.INSERT_ERROR}"
                                            )
                                        })
                                compositeDisposable.add(insertDisposable)
                            }, {
                                Log.e(
                                    Constants.LogTag.MESSAGES_AND_REACTIONS,
                                    "MESSAGE ${Constants.LogMessage.DELETE_ERROR}"
                                )
                            })
                    loadedIsLast = result.foundNewest
                    if (loadedIsLast) subscribeTopicRefresher(topic = topic, stream = stream)

                    currentMessageList.addAll(newMessageList)
                    view.showMessages(currentMessageList)

                    compositeDisposable.add(deleteDisposable)
                },
                {
                    view.showError(throwable = it, error = it.toErrorType())
                })

        compositeDisposable.add(disposable)
    }

    private fun initRefreshLoadForStreamMessages(
        stream: Stream,
        messages: List<SelectViewTypeClass.Message>,
    ) {
        view.showMessages(messages = messages)
        @Suppress("UNCHECKED_CAST") val disposable = model.loadStreamMessages(
            stream = stream, anchor = "${messages.first().id}",
            numAfter = Constants.LIMIT_MESSAGE_COUNT_FOR_TOPIC,
            numBefore = 0
        )
            .subscribe(
                { result ->
                    val newMessageList = result.messages
                    val deleteDisposable =
                        model.deleteMessagesFromStream(stream = stream)
                            .subscribe({
                                val insertDisposable =
                                    model.insertAllMessagesAndReactions(messages = newMessageList)
                                        .subscribe({ }, {
                                            Log.e(
                                                Constants.LogTag.MESSAGES_AND_REACTIONS,
                                                "MESSAGE ${Constants.LogMessage.INSERT_ERROR}"
                                            )
                                        })

                                compositeDisposable.add(insertDisposable)
                            }, {
                                Log.e(
                                    Constants.LogTag.MESSAGES_AND_REACTIONS,
                                    "MESSAGE ${Constants.LogMessage.DELETE_ERROR}"
                                )
                            })

                    compositeDisposable.add(deleteDisposable)
                    loadedIsLast = result.foundNewest
                    if (loadedIsLast) subscribeStreamsRefresher(stream = stream)

                    currentMessageList.addAll(newMessageList)
                    view.showMessages(messages = currentMessageList)
                },
                {
                    view.showError(throwable = it, error = it.toErrorType())
                })

        compositeDisposable.add(disposable)
    }

    private fun refreshReactionOnMessage(message: SelectViewTypeClass.Message) {
        val deleteDisposable = model.deleteReactionsByMessageId(messageId = message.id)
            .subscribe({
                val insertDisposable = model.insertAllReactions(message.id, message.reactions)
                    .subscribe({}, {
                        Log.e(
                            Constants.LogTag.MESSAGES_AND_REACTIONS,
                            "REACTIONS ${Constants.LogMessage.INSERT_ERROR}"
                        )
                    })

                compositeDisposable.add(insertDisposable)
            }, {
                Log.e(
                    Constants.LogTag.MESSAGES_AND_REACTIONS,
                    "REACTIONS ${Constants.LogMessage.DELETE_ERROR}"
                )
            })

        compositeDisposable.add(deleteDisposable)
    }

}
