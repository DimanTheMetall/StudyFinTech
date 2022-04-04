package com.example.homework2.viewmodels

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.homework2.R
import com.example.homework2.dataclasses.Reaction
import com.example.homework2.dataclasses.Stream
import com.example.homework2.dataclasses.Topic
import com.example.homework2.dataclasses.chatdataclasses.*
import com.example.homework2.retrofit.RetrofitService
import com.example.homework2.zulipApp
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class ChatViewModel : ViewModel() {

    private var currentId = 0

    private var chatListList = emptyList<SelectViewTypeClass.Chat.Message>()

    private val compositeDisposable = CompositeDisposable()

    val chatObservable: Observable<SelectViewTypeClass> get() = chatSubject
    private val chatSubject = BehaviorSubject.create<SelectViewTypeClass>().apply {
        onNext(SelectViewTypeClass.Progress)
    }

    fun getPresense(retrofitService: RetrofitService, userEmail: String): String {
        var resultString: String = "offline"

        val presenceDisposalbe = retrofitService.getPresense(userEmail)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultString = if (it.presence.website.timestamp > 500) {
                    "offline"
                } else {
                    it.presence.website.status
                }
            }, { resultString = "offline" })

        presenceDisposalbe.dispose()

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
                    {},
                    {})

        sendMessageDisposable.dispose()
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

//    fun onEmojiClick(emoji: String, position: Int) {
//        val newList = chatListList.toMutableList()
//        val emojiList =
//            (newList[position] as? SelectViewTypeClass.Message)?.emojiList?.toMutableSet()
//                ?: return
//        emojiList.add(Reaction(emoji, 1))
//        newList.updateEmoji(emojiList.toList(), position)
//        chatListList = newList
//        updateChatList()
//    }

    fun updateEmoji(position: Int, reaction: Reaction) {
        val chatListMutable = chatListList.toMutableList()
        val emojiList =
            (chatListMutable[position] as? SelectViewTypeClass.Chat.Message)?.emojiList?.toMutableSet()
                ?: return
        val hasReaction = emojiList.contains(reaction)

        if (hasReaction) {
            emojiList.remove(reaction)
        } else {
            emojiList.add(reaction)
        }

        chatListMutable.updateEmoji(emojiList.toList(), position)
        chatListList = chatListMutable
        updateChatList()
    }

    private fun MutableList<SelectViewTypeClass.Chat.Message>.updateEmoji(
        emojiList: List<Reaction>,
        position: Int
    ) {
        (this[position] as? SelectViewTypeClass.Chat.Message)?.copy(emojiList = emojiList)?.let {
            this[position] = it
        }
    }

//    fun onNextMassageClick(messageText: String) {
//        val list = chatListList.toMutableList()
//        list.add(
//            SelectViewTypeClass.Message(
//                currentId,
//                messageText,
//                "You Name",
//                2,
//                true
//            )
//        )
//        currentId++
//        chatListList = list
//        updateChatList()
//    }

    private fun updateChatList() {
        chatSubject.onNext(SelectViewTypeClass.Success(chatListList))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
