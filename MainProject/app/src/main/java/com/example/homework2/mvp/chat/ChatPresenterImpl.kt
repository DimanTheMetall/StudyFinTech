package com.example.homework2.mvp.chat

import android.view.View
import com.example.homework2.Constance
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
        Constance.INIT_REFRESHER_DELAY,
        Constance.DOWNLOAD_MESSAGES_PERIOD,
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
                    }, { view.showError(throwable = it, error = it.toErrorType()) })

                compositeDisposable.add(deleteDisposable)
            }, { view.showError(throwable = it, error = it.toErrorType()) })

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
                        }, { view.showError(throwable = it, error = it.toErrorType()) })

                    compositeDisposable.add(addDisposable)
                }, { view.showError(throwable = it, error = it.toErrorType()) })

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
                    .subscribe({}, {})

                compositeDisposable.add(deleteFromDBDisposable)
            }, { view.showError(throwable = it, error = it.toErrorType()) })
        compositeDisposable.add(deleteFromZulipDisposable)
    }

    override fun onAddReactionMessageClick(message: SelectViewTypeClass.Message) {
        view.hideMessageBottomDialog()
        view.showReactionDialog()
    }

    override fun onAddInMessageClick() {
        view.showReactionDialog()
    }

    override fun onMessageLongClick(message: SelectViewTypeClass.Message) {
        view.showMessageBottomDialog(message = message)
    }

    override fun onInTopicCLick(topic: Topic, stream: Stream) {
        view.openFrag(ChatFragment.newInstance(topic = topic, stream = stream))
    }

    override fun onTopicMessagesNextPageLoadRequested(stream: Stream, topic: Topic) {
        if (!loadedIsLast) for (i in 1..100) {
            loadNextTopicMessages(stream = stream, topic = topic)
        }
    }

    override fun onTopicMessagePreviousPageLoadRequest(stream: Stream, topic: Topic) {
        if (!loadedIsFirst) loadPreviousTopicsMessages(stream = stream, topic = topic)
    }

    override fun onStreamMessageNextPgeLoadRequest(stream: Stream) {
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
                        currentMessageList.addAll(messages)
                        if (!isStreamChat) {
                            checkAndDelete(stream = stream, topic = topic)
                        } else {
                            updateHelpTopicList(streamId = stream.streamId)
                        }
                        view.showMessages(currentMessageList)
                    }, { view.showError(throwable = it, error = it.toErrorType()) })

                compositeDisposable.add(loadDisposable)
            }, { view.showError(throwable = it, error = it.toErrorType()) })

        compositeDisposable.add(sendDisposable)
    }

    override fun onInitMessageForTopicRequest(stream: Stream, topic: Topic) {
        val disposable = model.getMessageFromDataBase(stream = stream, topic = topic)
            .subscribe({ messages ->
                if (messages.isNullOrEmpty()) {
                    loadNextTopicMessages(stream = stream, topic = topic)
                } else {
                    currentMessageList.addAll(messages)
                    view.showMessages(messages = currentMessageList)
                }
            }, { view.showError(throwable = it, error = Errors.SYSTEM) })

        compositeDisposable.add(disposable)
    }

    override fun onInitMessageForStreamRequest(stream: Stream) {
        loadNextStreamMessages(stream = stream)
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
                        view.showMessages(messages = newList)
                    }, {
                        view.showError(throwable = it, error = it.toErrorType())
                    })
                compositeDisposable.add(messageDisposable)
            }, { view.showError(throwable = it, error = it.toErrorType()) })

        compositeDisposable.add(emojiDisposable)
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

    override fun onApplyEditMessageClick(message: SelectViewTypeClass.Message) {
        view.hideEditMessageDialog()
        view.showProgress()
        editMessage(message = message, isTopicChanged = false)
    }

    override fun onCancelEditMessageClick() {
        view.hideEditMessageDialog()
    }

    override fun onChangeTopicClick(message: SelectViewTypeClass.Message, stream: Stream) {
        view.hideMessageBottomDialog()
        view.showChangeTopicDialog(message = message, stream = stream)
    }

    override fun onApplyChangeTopicForMessage(message: SelectViewTypeClass.Message) {
        view.hideChangeTopicDialog()
        view.showProgress()
        editMessage(message = message, isTopicChanged = true)
    }

    override fun onCopyCLick() {
        view.hideMessageBottomDialog()
    }

    override fun onInit() {
        currentMessageList.clear()
        loadedIsLast = false
    }

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

    private fun updateHelpTopicList(streamId: Int) {
        val disposable = model.loadTopicList(streamId = streamId)
            .subscribe({ view.setTopicListInStream(topicList = it.topics) },
                { view.showError(throwable = it, error = it.toErrorType()) })

        compositeDisposable.add(disposable)
    }

    private fun onReactionUpdateMapList(changedMessage: SelectViewTypeClass.Message) {
        val newList =
            currentMessageList.map { oldMessage -> if (oldMessage.id == changedMessage.id) changedMessage else oldMessage }
        currentMessageList = newList.toMutableList()
    }

    private fun loadPreviousStreamMessages(stream: Stream) {
        if (!loadedIsFirst) {
            view.showProgress()
            val disposable = model.loadStreamMessages(
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
                    view.showMessages(messages = currentMessageList)
                }, { view.showError(throwable = it, error = it.toErrorType()) })

            compositeDisposable.add(disposable)
        }
    }

    private fun loadNextStreamMessages(stream: Stream) {
        val lastMessageId =
            if (currentMessageList.isNullOrEmpty()) 1L else currentMessageList.last().id
        view.showProgress()
        val disposable = model.loadStreamMessages(
            stream = stream,
            anchor = lastMessageId.toString(),
            numAfter = Constance.MESSAGES_COUNT_PAGINATION,
            numBefore = 0
        )
            .subscribe({ json ->
                val messages = json.messages.filterNot { it.id == lastMessageId }
                loadedIsLast = json.foundNewest
                if (loadedIsLast) subscribeStreamsRefresher(stream = stream)

                currentMessageList.addAll(messages.sortedBy { it.timestamp })
                view.showMessages(currentMessageList)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(disposable)
    }

    private fun loadNextTopicMessages(stream: Stream, topic: Topic) {
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
                    if (loadedIsLast) subscribeTopicRefresher(topic = topic, stream = stream)

                    currentMessageList.addAll(messages)
                    view.showMessages(currentMessageList)

                    val insertDisposable = model.insertAllMessagesAndReactions(messages = messages)
                        .subscribe(
                            { checkAndDelete(stream = stream, topic = topic) },
                            { view.showError(throwable = it, error = Errors.SYSTEM) })
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
            numBefore = Constance.MESSAGES_COUNT_PAGINATION
        )
            .subscribe({ json ->
                val newList = json.messages
                    .filterNot { it.id == currentMessageList.first().id }.toMutableList()
                newList.addAll(currentMessageList)
                currentMessageList = newList
                view.showMessages(messages = currentMessageList)
            }, { view.showError(throwable = it, error = it.toErrorType()) })

        compositeDisposable.add(disposable)
    }

    private fun subscribeTopicRefresher(topic: Topic, stream: Stream) {
        if (!refresherIsSubscribed) {
            val refreshDisposable = refresherObservable
                .subscribe({ loadNextTopicMessages(stream = stream, topic = topic) }, {
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

    private fun editMessageInList(
        newMessage: SelectViewTypeClass.Message,
        list: MutableList<SelectViewTypeClass.Message>,
        topicIsChanged: Boolean
    ): List<SelectViewTypeClass.Message> {
        var newList: List<SelectViewTypeClass.Message> = list
        if (topicIsChanged) {
            newList = list.map { oldMessage ->
                if (newMessage.id != oldMessage.id) oldMessage else oldMessage.copy(
                    content = newMessage.content
                )
            }
        } else {
            newList.toMutableList().remove(newMessage)
        }
        return newList
    }

    private fun updateMessageInDB(message: SelectViewTypeClass.Message) {
        val updateDisposable = model.updateMessageInDB(message)
            .subscribe({}, {})

        compositeDisposable.add(updateDisposable)
    }

    private fun insertSingleMessageInDB(message: SelectViewTypeClass.Message) {
        val disposable = model.insertSingleMessageInDB(message)
            .subscribe({}, {})

        compositeDisposable.add(disposable)
    }

    private fun editMessage(message: SelectViewTypeClass.Message, isTopicChanged: Boolean) {
        val editDisposable = model.editMessageInZulip(message = message)
            .subscribe({
                updateMessageInDB(message = message)
                view.showMessages(
                    editMessageInList(
                        newMessage = message,
                        list = currentMessageList,
                        topicIsChanged = isTopicChanged
                    )
                )
            }, { view.showError(throwable = it, error = it.toErrorType()) })

        compositeDisposable.add(editDisposable)
    }

}
