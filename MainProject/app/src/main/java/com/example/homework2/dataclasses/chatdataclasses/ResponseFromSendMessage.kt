package com.example.homework2.dataclasses.chatdataclasses

data class ResponseFromSendMessage(
    val code: String,
    val msg: String,
    val result: String
)