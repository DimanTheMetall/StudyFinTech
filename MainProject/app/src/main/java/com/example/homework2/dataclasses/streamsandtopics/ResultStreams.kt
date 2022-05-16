package com.example.homework2.dataclasses.streamsandtopics

import com.google.gson.annotations.SerializedName

class ResultStreams(
    @SerializedName("result")
    val result: String,

    @SerializedName("streams", alternate = ["subscriptions"])
    val streams: List<Stream>,
)
