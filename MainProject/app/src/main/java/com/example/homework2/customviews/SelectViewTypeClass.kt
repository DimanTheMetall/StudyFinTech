package com.example.homework2.customviews

sealed class SelectViewTypeClass {

    data class Date(
        val time: String
    ) : SelectViewTypeClass()

    data class Message(
        val id: Int,
        val textMessage: String,
        val titleMessage: String,
        val imageURL: Int,
        val isYou: Boolean = true,
        val emojiList: MutableList<Reaction> = mutableListOf()
    ) : SelectViewTypeClass()
}
