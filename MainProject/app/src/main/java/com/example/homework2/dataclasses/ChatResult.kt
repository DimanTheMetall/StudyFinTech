package com.example.homework2.dataclasses

sealed class ChatResult {

    data class Success(val chatList: List<SelectViewTypeClass>) : ChatResult()

    object Progress: ChatResult()

    object Error: ChatResult()
}
