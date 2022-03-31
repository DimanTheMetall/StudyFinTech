package com.example.homework2.dataclasses

data class JsonSubscribesStreams(
    val msg: String,
    val result: String,
    val subscriptions: List<Subscription>
)