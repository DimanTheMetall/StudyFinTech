package com.example.homework2.dataclasses.streamsandtopics

import com.google.gson.annotations.SerializedName

data class JsonUsers(

    @SerializedName("members")
    val members: List<Member>,

    @SerializedName("msg")
    val msg: String,

    @SerializedName("result")
    val result: String
)
