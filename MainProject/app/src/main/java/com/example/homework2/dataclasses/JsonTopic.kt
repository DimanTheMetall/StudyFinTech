package com.example.homework2.dataclasses

data class JsonTopic(
    val msg: String,
    val result: String,
    val topics: List<Topic>
)
