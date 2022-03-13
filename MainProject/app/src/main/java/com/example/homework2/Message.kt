package com.example.homework2

data class Message(
    val textMessage: String,
    val titleMessage: String,
    val imageId: Int,
    val isYou: Boolean = true
)
