package com.example.homework2.dataclasses.chatdataclasses

data class JsonMessages(
    val anchor: Int,
    val found_anchor: Boolean,
    val found_newest: Boolean,
    val messages: List<SelectViewTypeClass.Chat.Message>,
    val msg: String,
    val result: String
)