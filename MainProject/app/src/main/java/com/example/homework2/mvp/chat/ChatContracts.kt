package com.example.homework2.mvp.chat

import androidx.fragment.app.Fragment
import com.example.homework2.Errors
import com.example.homework2.dataclasses.chatdataclasses.JsonMessages
import com.example.homework2.dataclasses.chatdataclasses.JsonResponse
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.JsonTopic
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Completable
import io.reactivex.Single

interface ChatView : BaseView {

    fun showError(throwable: Throwable, error: Errors)

    fun showProgress()

    fun showMessages(messages: List<SelectViewTypeClass.Message>)

    fun changeHelpVisibility(visibility: Int)

    fun fillTopicField(topic: Topic)

    fun openFrag(fragment: Fragment, tag: String? = null)

    fun setTopicListInStream(topicList: List<Topic>)

    fun showReactionDialog()

    fun hideReactionDialog()

    fun showMessageBottomDialog(message: SelectViewTypeClass.Message)

    fun hideMessageBottomDialog()

    fun showEditMessageDialog(message: SelectViewTypeClass.Message)

    fun hideEditMessageDialog()

}

interface ChatPresenter : BasePresenter {

    fun onEmojiInMessageClick(
        messageId: Long,
        emojiName: String,
        reactionType: String,
        isSelected: Boolean
    )

    fun onDeleteMessageClick(message: SelectViewTypeClass.Message)

    fun onAddReactionMessageClick(message: SelectViewTypeClass.Message)

    fun onAddInMessageClick()

    fun onMessageLongClick(message: SelectViewTypeClass.Message)

    fun onInTopicCLick(topic: Topic, stream: Stream)

    fun onTopicMessagesNextPageLoadRequested(stream: Stream, topic: Topic)

    fun onTopicMessagePreviousPageLoadRequest(stream: Stream, topic: Topic)

    fun onStreamMessageNextPgeLoadRequest(stream: Stream)

    fun onStreamMessagePreviousPgeLoadRequest(stream: Stream)

    fun onSendMessageInTopicRequest(
        sentText: String,
        topic: Topic,
        stream: Stream,
        isStreamChat: Boolean
    )

    fun onInitMessageForTopicRequest(stream: Stream, topic: Topic)

    fun onInitMessageForStreamRequest(stream: Stream)

    fun onEmojiInSheetDialogClick(messageId: Long, emojiName: String, reactionType: String)

    fun onHelpTopicItemCLick(topic: Topic)

    fun onCancelHelpBtnCLick()

    fun onFocusChanged(isFocused: Boolean)

    fun onEditMessageClick(message: SelectViewTypeClass.Message)

    fun onApplyEditMessageClick(message: SelectViewTypeClass.Message)

    fun onCancelEditMessageClick()

}

interface ChatModel : BaseModel {

    fun deleteMessageFromDB(message: SelectViewTypeClass.Message): Completable

    fun deleteMessageFromZulip(messageId: Long): Single<JsonResponse>

    fun loadTopicList(streamId: Int): Single<JsonTopic>

    fun loadMessageById(messageId: Long): Single<SelectViewTypeClass.Message>

    fun deleteEmoji(messageId: Long, emojiName: String, reactionType: String): Single<JsonResponse>

    fun addEmoji(messageId: Long, emojiName: String, reactionType: String): Single<JsonResponse>

    fun sendMessage(sentText: String, topic: Topic, stream: Stream): Single<JsonResponse>

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

    fun loadLastMessage(
        topic: Topic,
        stream: Stream
    ): Single<List<SelectViewTypeClass.Message>>

    fun insertAllMessagesAndReactions(messages: List<SelectViewTypeClass.Message>): Completable

    fun deleteOldestMessagesWhereIdLess(messageIdToSave: Long, stream: Stream, topic: Topic)

    fun getMessageFromDataBase(
        stream: Stream,
        topic: Topic
    ): Single<MutableList<SelectViewTypeClass.Message>>

    fun editMessageInZulip(message: SelectViewTypeClass.Message): Single<JsonResponse>

    fun updateMessageInDB(message: SelectViewTypeClass.Message): Completable

    fun insertSingleMessageInDB(message: SelectViewTypeClass.Message)

}
