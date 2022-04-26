package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.annotations.SerializedName

class JsonMessages(

    @SerializedName("found_newest")
    val foundNewest: Boolean,

    @SerializedName("found_oldest")
    val foundOldest: Boolean,

    @SerializedName("messages")
    val messages: List<SelectViewTypeClass.Message>,

    @SerializedName("result")
    val result: String
)
