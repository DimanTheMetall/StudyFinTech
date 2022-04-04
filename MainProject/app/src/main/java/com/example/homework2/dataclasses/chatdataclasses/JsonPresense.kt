package com.example.homework2.dataclasses.chatdataclasses

data class JsonPresense(
    val msg: String,
    val presence: Presence,
    val result: String
)