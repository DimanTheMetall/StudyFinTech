package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.annotations.SerializedName

class JsonMessages(

    @SerializedName("anchor")
    val anchor: Long,

    @SerializedName("found_anchor")
    val foundAnchor: Boolean,

    @SerializedName("found_newest")
    val foundNewest: Boolean,

    @SerializedName("messages")
    val messages: List<SelectViewTypeClass.Chat.Message>,

    @SerializedName("msg")
    val msg: String,

    @SerializedName("result")
    val result: String
)