package com.example.homework2.dataclasses

data class Channel(
    val name: String,
    var topicList: MutableList<ChannelTopic>
)
