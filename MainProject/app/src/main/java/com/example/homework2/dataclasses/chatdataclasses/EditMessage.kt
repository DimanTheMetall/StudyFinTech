package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.annotations.SerializedName

class EditMessage(
    @SerializedName("message_id")
    val messageId: Long,

    @SerializedName("topic")
    val topic: String,

    @SerializedName("content")
    val content: String
)
