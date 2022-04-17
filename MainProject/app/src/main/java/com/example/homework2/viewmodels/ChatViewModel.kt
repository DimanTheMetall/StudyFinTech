package com.example.homework2.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homework2.Constance
import com.example.homework2.data.ZulipDataBase
import com.example.homework2.data.local.entity.MessageEntity
import com.example.homework2.data.local.entity.ReactionEntity
import com.example.homework2.dataclasses.Stream
import com.example.homework2.dataclasses.Topic
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass
import com.example.homework2.dataclasses.chatdataclasses.SendMessage
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class ChatViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private var loadedIsLast = false

    val chatObservable: Observable<SelectViewTypeClass> get() = chatSubject
    private val chatSubject = BehaviorSubject.create<SelectViewTypeClass>().apply {
        onNext(SelectViewTypeClass.Progress)
    }

    lateinit var dataBase: ZulipDataBase
//
//    fun initDateBase(
//        context: Context,
//        topic: Topic,
//        stream: Stream,
//        retrofitService: RetrofitService
//    ) {
//        messageList.clear()
//        dataBase = ZulipDataBase.getInstance(context)
//        selectMessagesAndReactionOnTopic(
//            topic = topic, stream = stream,
//            retrofitService = retrofitService
//        )
//    }

    val messageList = mutableListOf<SelectViewTypeClass.Chat.Message>()

    private fun onUpdateList(
        topicName: String,
        streamId: Int
    ) {
        val oldestOnSaveMessageId =
            if (messageList.lastIndex - 50 < 0) 0L else messageList[messageList.lastIndex - 50].id

        val disposable = dataBase.getMessagesAndReactionDao()
            .deleteOldestMessages(
                oldestMessagedItAfterDeleted = oldestOnSaveMessageId,
                streamId = streamId,
                topicName = topicName
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        compositeDisposable.add(disposable)
    }

//    private fun selectMessagesAndReactionOnTopic(
//        topic: Topic,
//        stream: Stream,
//        retrofitService: RetrofitService
//    ) {
//        val resultMessages = mutableListOf<SelectViewTypeClass.Chat.Message>()
//        val disposable = dataBase.getMessagesAndReactionDao()
//            .selectMessagesAndReactionFromTopic(topicName = topic.name, streamId = stream.stream_id)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { map ->
//                resultMessages.clear()
//                map.keys.forEach { messageEntity ->
//                    val message = messageEntity.toMessage()
//                    val reactionList =
//                        map.getValue(messageEntity).map { it.toReaction() }
//                    message.reactions = reactionList
//                    resultMessages.add(message)
//                }
//            }
//
//        if (resultMessages.isNullOrEmpty()) {
//            loadTopicMessage(
//                retrofitService = retrofitService,
//                topic = topic,
//                stream = stream,
//                "1",
//                20,
//                0
//            )
//        } else {
//            chatSubject.onNext(SelectViewTypeClass.Success(messagesList = resultMessages))
//        }
////        compositeDisposable.add(disposable)
//    }

    private fun insertLatestMessagesAndReaction(
        messages: List<SelectViewTypeClass.Chat.Message>
    ) {

        val insertDisposableMessages = dataBase.getMessagesAndReactionDao()
            .insertMessagesFromTopic(messages.map { MessageEntity.toEntity(it) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        messages.forEach { message ->
            val insertDisposableReaction =
                dataBase.getMessagesAndReactionDao().insertAllReactionOnMessages(message.reactions
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
            chatSubject.onNext(SelectViewTypeClass.Success(messagesList = messages))
        compositeDisposable.add(insertDisposableMessages)
    }

    fun uploadNewReaction(
        retrofitService: RetrofitService,
        messageId: Long,
        emojiName: String,
        reactionType: String,
    ) {
        val reactionDisposable = retrofitService.addEmoji(messageId, emojiName, reactionType, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chatSubject.onNext(SelectViewTypeClass.UploadMessageSuccess)
            }, { chatSubject.onNext(SelectViewTypeClass.Error) })

        compositeDisposable.add(reactionDisposable)

    }

    fun deleteOrAddReaction(
        retrofitService: RetrofitService,
        messageId: Long,
        emojiName: String,
        reactionType: String,
        isSelected: Boolean
    ) {
        if (!isSelected) {
            val reactionDisposable = retrofitService.deleteEmoji(messageId, emojiName, reactionType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    chatSubject.onNext(SelectViewTypeClass.UploadMessageSuccess)
                }, { chatSubject.onNext(SelectViewTypeClass.Error) })

            compositeDisposable.add(reactionDisposable)
        } else {
            val reactionDisposable =
                retrofitService.addEmoji(messageId, emojiName, reactionType, null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ chatSubject.onNext(SelectViewTypeClass.UploadMessageSuccess) },
                        { chatSubject.onNext(SelectViewTypeClass.Error) })

            compositeDisposable.add(reactionDisposable)
        }
    }

    fun sendMessage(
        message: String,
        topic: Topic,
        stream: Stream,
        retrofitService: RetrofitService
    ) {
        val sentMessage = SendMessage(
            type = Constance.STREAM,
            to = stream.name,
            content = message,
            topic = topic.name
        )

        val sendMessageDisposable =
            retrofitService.sendMessage(
                type = sentMessage.type,
                to = sentMessage.to,
                content = sentMessage.content,
                topic = sentMessage.topic
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { chatSubject.onNext(SelectViewTypeClass.UploadMessageSuccess) },
                    { chatSubject.onNext(SelectViewTypeClass.Error) })

        compositeDisposable.add(sendMessageDisposable)
    }

//    fun loadTopicMessage(
//        retrofitService: RetrofitService,
//        topic: Topic,
//        stream: Stream,
//        lastMessageId: String,
//        numAfter: Int,
//        numBefore: Int
//    ) {
//        if (!loadedIsLast) {
//            chatSubject.onNext(SelectViewTypeClass.Progress)
//            val messagesDisposable =
//                retrofitService.getMessages(
//                    narrow = Narrow(
//                        listOf(
//                            Filter(operator = Constance.STREAM, operand = stream.name),
//                            Filter(operator = Constance.TOPIC, operand = topic.name),
//                        )
//                    ).toJson(),
//                    anchor = lastMessageId,
//                    numBefore = numBefore,
//                    numAfter = numAfter,
//                    false
//                )
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        val messages = it.messages.filterNot { it.id == lastMessageId }
//                        messageList.addAll(messages)
//                        insertLatestMessagesAndReaction(messages = messages)
//                        loadedIsLast = it.foundNewest
//                    },
//                        {
//                            SelectViewTypeClass.Error
//                        })
//            onUpdateList(topicName = topic.name, streamId = stream.stream_id)
//            compositeDisposable.add(messagesDisposable)
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
