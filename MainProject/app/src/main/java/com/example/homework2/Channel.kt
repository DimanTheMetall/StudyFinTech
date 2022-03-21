package com.example.homework2

data class Channel(
    val name: String,
    var topicList: MutableList<ChannelTopic>
)
