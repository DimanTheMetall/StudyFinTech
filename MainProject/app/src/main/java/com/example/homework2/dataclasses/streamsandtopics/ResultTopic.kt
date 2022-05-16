package com.example.homework2.dataclasses.streamsandtopics

import com.google.gson.annotations.SerializedName

class ResultTopic(
    @SerializedName("result")
    val result: String,

    @SerializedName("topics")
    val topics: List<Topic>,
)
