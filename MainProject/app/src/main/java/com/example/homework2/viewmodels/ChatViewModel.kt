package com.example.homework2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.ChatResult
import com.example.homework2.dataclasses.Reaction
import com.example.homework2.dataclasses.SelectViewTypeClass
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class ChatViewModel : ViewModel() {

    private var currentId = 0

    private var chatListList = listOf<SelectViewTypeClass>(
        SelectViewTypeClass.Message(
            1,
            "textMessage",
            "titleMessage",
            2,
            false,
            emptyList<Reaction>().toMutableList()
        ),
        SelectViewTypeClass.Message(
            2,
            "textMessage",
            "titleMessage",
            2,
            true,
            emptyList<Reaction>().toMutableList()
        ),
        SelectViewTypeClass.Date("time 02")
    )

    private val compositeDisposable = CompositeDisposable()

    val chatObservable: Observable<ChatResult> get() = chatSubject
    private val chatSubject = BehaviorSubject.create<ChatResult>().apply {
        onNext(ChatResult.Success(chatListList))
    }

    fun onEmojiClick(emoji: String, position: Int) {
        val newList = chatListList.toMutableList()
        val emojiList =
            (newList[position] as? SelectViewTypeClass.Message)?.emojiList?.toMutableSet()
                ?: return
        emojiList.add(Reaction(emoji, 1))
        newList.updateEmoji(emojiList.toList(), position)
        chatListList = newList
        updateChatList()
    }

    fun updateEmoji(position: Int, reaction: Reaction) {
        val chatListMutable = chatListList.toMutableList()
        val emojiList =
            (chatListMutable[position] as? SelectViewTypeClass.Message)?.emojiList?.toMutableSet()
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

    private fun MutableList<SelectViewTypeClass>.updateEmoji(
        emojiList: List<Reaction>,
        position: Int
    ) {
        (this[position] as? SelectViewTypeClass.Message)?.copy(emojiList = emojiList)?.let {
            this[position] = it
        }
    }

    fun onNextMassageClick(messageText: String) {
        val list = chatListList.toMutableList()
        list.add(
            SelectViewTypeClass.Message(
                currentId,
                messageText,
                "You Name",
                2,
                true
            )
        )
        currentId++
        chatListList = list
        updateChatList()
    }

    private fun updateChatList() {
        chatSubject.onNext(ChatResult.Success(chatListList))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
