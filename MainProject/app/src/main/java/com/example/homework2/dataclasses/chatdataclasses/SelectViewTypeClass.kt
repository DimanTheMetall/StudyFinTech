package com.example.homework2.dataclasses.chatdataclasses

sealed class SelectViewTypeClass {

    object Error : SelectViewTypeClass()

    object Progress : SelectViewTypeClass()

    object UploadMessageSuccess : SelectViewTypeClass()

    data class Success(val messagesList: List<Chat.Message>) : SelectViewTypeClass()

    sealed class Chat {

        data class Date(
            val time: String
        ) : Chat()

        data class Message(
            val avatar_url: String,
            val client: String,
            val content: String,
            val content_type: String,
            val id: Long,
            val sender_email: String,
            val sender_full_name: String,
            val sender_id: Int,
            val stream_id: Int,
            val subject: String,
            val timestamp: Int,
            val type: String,
            var reactions: List<Reaction>

        ) : Chat()
    }
}
