package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.annotations.SerializedName

class ResultMessage(
    @SerializedName("message")
    val message: SelectViewTypeClass.Message,

    @SerializedName("result")
    val result: String,
)
