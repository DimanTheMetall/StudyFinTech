package com.example.homework2.dataclasses

import com.google.gson.annotations.SerializedName

data class JsonStreams(

    @SerializedName("invite_only")
    val inviteOnly: Boolean? = null,

    @SerializedName("invite_only")
    val msg: String,

    @SerializedName("result")
    val result: String,

    @SerializedName("streams", alternate = ["subscriptions"])
    val streams: List<Stream>
)
