package com.example.homework2.mvp.chat

import com.example.homework2.data.local.entity.MessageEntity
import com.example.homework2.data.local.entity.ReactionEntity
import com.example.homework2.dataclasses.chatdataclasses.JsonMessages
import com.example.homework2.dataclasses.chatdataclasses.JsonRespone
import com.example.homework2.dataclasses.chatdataclasses.ResponseFromSendMessage
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Single

interface ChatView : BaseView {

    fun initBottomSheetDialog()

    fun initClickListenerOnMessageTranslateImage()

    fun initImageSwitcher()

    fun initArguments()

    fun initRecycleAdapter()

    fun configureRecycleAdapter()

    fun initShimmer()

    fun showError()

    fun showProgress()

    fun showMessages(messages: List<SelectViewTypeClass.Chat.Message>)

}

interface ChatPresenter : BasePresenter {

    fun checkAndDelete(stream: Stream, topic: Topic)

    fun onEmojiInMessageClick(
        messageId: Long,
        emojiName: String,
        reactionType: String,
        isSelected: Boolean
    )

    fun onMessagesLoadRequested(stream: Stream, topic: Topic)

    fun onSendMessageRequest(sentText: String, topic: Topic, stream: Stream)

    fun onInitMessageRequest(stream: Stream, topic: Topic)

    fun onEmojiInSheetDialogClick(messageId: Long, emojiName: String, reactionType: String)

}

interface ChatModel : BaseModel {

    fun loadMessageById(messageId: Long): Single<SelectViewTypeClass.Chat.Message>

    fun deleteEmoji(messageId: Long, emojiName: String, reactionType: String): Single<JsonRespone>

    fun addEmoji(messageId: Long, emojiName: String, reactionType: String): Single<JsonRespone>

    fun sendMessage(sentText: String, topic: Topic, stream: Stream): Single<ResponseFromSendMessage>

    fun loadTopicMessages(
        topic: Topic,
        stream: Stream,
        lastMessageId: String,
        numAfter: Int,
        numBefore: Int
    ): Single<JsonMessages>

    fun loadLastMessage(
        topic: Topic,
        stream: Stream
    ): Single<List<SelectViewTypeClass.Chat.Message>>

    fun insertAllMessagesAndReactions(messages: List<SelectViewTypeClass.Chat.Message>)

    fun deleteOldestMessagesWhereIdLess(messageIdToSave: Long, stream: Stream, topic: Topic)

    fun selectMessage(
        stream: Stream,
        topic: Topic
    ): Single<Map<MessageEntity, List<ReactionEntity>>>

}