package com.example.homework2.customviews

sealed class SelectViewTypeClass {

    data class Data(
        val time: String
    ) : SelectViewTypeClass()

    data class Message(
        val textMessage: String,
        val titleMessage: String,
        val imageId: Int,
        val isYou: Boolean = true,
        val emoji: MutableList<Reaction> = mutableListOf()
    ) : SelectViewTypeClass()
}