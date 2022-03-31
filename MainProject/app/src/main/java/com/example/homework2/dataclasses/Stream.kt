package com.example.homework2.dataclasses

data class Stream(
    val stream_id: Int,
    val invite_only: Boolean,
    val name: String,
    var topicList: MutableList<Topic> = mutableListOf()
)


