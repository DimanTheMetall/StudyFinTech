package com.example.homework2.dataclasses

import com.google.gson.annotations.SerializedName

data class JsonStreams(
    val invite_only: Boolean? = null,
    val msg: String,
    val result: String,
    @SerializedName("streams", alternate = ["subscriptions"])
    val streams: List<Stream>
)
