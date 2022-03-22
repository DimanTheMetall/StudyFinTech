package com.example.homework2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Reaction
import com.example.homework2.dataclasses.SelectViewTypeClass

class ChatViewModel() : ViewModel() {
    private var currentId = 0


    private val _chatList = MutableLiveData<List<SelectViewTypeClass>>().apply {
        value = listOf(
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
    }
    val chatList: LiveData<List<SelectViewTypeClass>> = _chatList

    init {
        _chatList.observeForever { list ->
            list.filterIsInstance<SelectViewTypeClass.Message>().lastOrNull()?.id?.let {
                currentId = it+1
            }

        }

    }


    fun onEmojiClick(emoji: String, position: Int) {
        val newList = _chatList.value?.toMutableList() ?: return
        val emojiList =
            (newList[position] as? SelectViewTypeClass.Message)?.emojiList?.toMutableSet()
                ?: return
        emojiList.add(Reaction(emoji, 1))

        newList.updateEmoji(emojiList.toList(), position)
        _chatList.value = newList
    }

    fun updateEmoji(position: Int, reaction: Reaction) {
        val chatListMutable = _chatList.value?.toMutableList() ?: return
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
        _chatList.value = chatListMutable
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
        val list = chatList.value?.toMutableList() ?: return
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
        _chatList.value = list
    }
}
