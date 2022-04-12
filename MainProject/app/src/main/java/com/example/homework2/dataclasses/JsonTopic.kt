package com.example.homework2.dataclasses

import com.google.gson.annotations.SerializedName

data class JsonTopic(

    @SerializedName("msg")
    val msg: String,

    @SerializedName("result")
    val result: String,

    @SerializedName("topics")
    val topics: List<Topic>
)
