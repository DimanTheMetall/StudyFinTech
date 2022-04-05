package com.example.homework2.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Reaction
import com.example.homework2.dataclasses.Stream
import com.example.homework2.dataclasses.Topic
import com.example.homework2.dataclasses.chatdataclasses.*
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class ChatViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val observeDisposableChat = CompositeDisposable()

    val chatObservable: Observable<SelectViewTypeClass> get() = chatSubject
    private val chatSubject = BehaviorSubject.create<SelectViewTypeClass>().apply {
        onNext(SelectViewTypeClass.Progress)
    }

    fun uploadNewReaction(
        retrofitService: RetrofitService,
        messageId: Int,
        emojiName: String,
        reactionType: String,
        emojiCode: String?
    ) {
        val reactionDisposable = retrofitService.addEmoji(messageId, emojiName, reactionType, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chatSubject.onNext(SelectViewTypeClass.UploadSuccess)
            }, { chatSubject.onNext(SelectViewTypeClass.Error) })

        compositeDisposable.add(reactionDisposable)
    }

    fun deleteOrAddReaction(
        retrofitService: RetrofitService,
        messageId: Int,
        emojiName: String,
        reactionType: String,
        isSelected: Boolean
    ) {
        if (!isSelected) {
            val reactionDisposable = retrofitService.deleteEmoji(messageId, emojiName, reactionType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    chatSubject.onNext(SelectViewTypeClass.UploadSuccess)
                }, { chatSubject.onNext(SelectViewTypeClass.Error) })

            compositeDisposable.add(reactionDisposable)
        } else {
            val reactionDisposable =
                retrofitService.addEmoji(messageId, emojiName, reactionType, null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ chatSubject.onNext(SelectViewTypeClass.UploadSuccess) },
                        { chatSubject.onNext(SelectViewTypeClass.Error) })

            compositeDisposable.add(reactionDisposable)
        }
    }

    fun getPresense(retrofitService: RetrofitService, userEmail: String): String {
        var resultString: String = "offline"

        val presenceDisposalbe = retrofitService.getPresence(userEmail)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultString = if (it.presence.website.timestamp > 500) {
                    "offline"
                } else {
                    it.presence.website.status
                }
            }, { resultString = "offline" })

        compositeDisposable.add(presenceDisposalbe)

        return resultString
    }

    fun sendMessage(
        message: String,
        topic: Topic,
        stream: Stream,
        retrofitService: RetrofitService
    ) {
        val sentMessage: SendMessage = SendMessage(
            "stream",
            stream.name,
            message,
            topic.name
        )

        val sendMessageDisposable =
            retrofitService.sendMessage(
                sentMessage.type,
                sentMessage.to,
                sentMessage.content,
                sentMessage.topic
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { chatSubject.onNext(SelectViewTypeClass.UploadSuccess) },
                    { chatSubject.onNext(SelectViewTypeClass.Error) })

        compositeDisposable.add(sendMessageDisposable)
    }

    fun startObserveChat(retrofitService: RetrofitService, stream: String, topic: String) {
        val d =  Observable.interval(3, TimeUnit.SECONDS)
            .subscribe { loadTopicMessage(retrofitService, topic, stream) }
        observeDisposableChat.add(d)
    }

    fun stopObserveChat() {
        observeDisposableChat.clear()
    }

    fun loadTopicMessage(retrofitService: RetrofitService, topic: String, stream: String) {
        val messagesDisposable =
            retrofitService.getMessages(
                Narrow(
                    listOf(
                        Filter("stream", stream),
                        Filter("topic", topic),
                    )
                ).toJson(),
                "oldest",
                1000,
                1000,
                false
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ chatSubject.onNext(SelectViewTypeClass.Success(it.messages)) }, {
                    SelectViewTypeClass.Error
                })

        compositeDisposable.add(messagesDisposable)
    }

    private fun MutableList<SelectViewTypeClass.Chat.Message>.updateEmoji(
        emojiList: List<Reaction>,
        position: Int
    ) {
        (this[position] as? SelectViewTypeClass.Chat.Message)?.copy(reactions = emojiList)
            ?.let {
                this[position] = it
            }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
