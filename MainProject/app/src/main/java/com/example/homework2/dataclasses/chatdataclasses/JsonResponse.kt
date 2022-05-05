package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.annotations.SerializedName

class JsonResponse(
    @SerializedName("result")
    val result: String,

    @SerializedName("msg")
    val msg: String
)
