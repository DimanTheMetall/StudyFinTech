package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.annotations.SerializedName

class ResultResponse(
    @SerializedName("result")
    val result: String,
)
