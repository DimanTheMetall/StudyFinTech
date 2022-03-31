package com.example.homework2.dataclasses

data class JsonStreams(
    val invite_only: Boolean,
    val msg: String,
    val result: String,
    val streams: List<Stream>
)
