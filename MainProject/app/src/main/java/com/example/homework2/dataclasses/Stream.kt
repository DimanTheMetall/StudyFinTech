package com.example.homework2.dataclasses

import com.google.gson.annotations.SerializedName

data class Stream(
    val stream_id: Int,
    val invite_only: Boolean,
    val name: String,
    var topicList: MutableList<Topic> = mutableListOf(),

    val role: Int?=null,
    var subscribers: List<Int>? = null
)


