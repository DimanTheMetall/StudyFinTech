package com.example.homework2.dataclasses.chatdataclasses

data class JsonMessage(
    val message: SelectViewTypeClass.Chat.Message,
    val msg: String,
    val raw_content: String,
    val result: String
)