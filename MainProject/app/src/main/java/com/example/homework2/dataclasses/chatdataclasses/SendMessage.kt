package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.annotations.SerializedName

data class SendMessage(
    val type: String,
    val to: String,
    val content: String,
    val topic: String
)
