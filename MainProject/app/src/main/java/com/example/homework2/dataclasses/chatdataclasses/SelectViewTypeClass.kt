package com.example.homework2.dataclasses.chatdataclasses

import com.google.gson.annotations.SerializedName

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
            @SerializedName("avatar_url")
            val avatarUrl: String,

            @SerializedName("client")
            val client: String,

            @SerializedName("content")
            val content: String,

            @SerializedName("content_type")
            val contentType: String,

            @SerializedName("id")
            val id: Long,

            @SerializedName("sender_email")
            val senderEmail: String,

            @SerializedName("sender_full_name")
            val senderFullName: String,

            @SerializedName("sender_id")
            val senderId: Int,

            @SerializedName("stream_id")
            val streamId: Int,

            @SerializedName("subject")
            val subject: String,

            @SerializedName("timestamp")
            val timestamp: Int,

            @SerializedName("type")
            val type: String,

            @SerializedName("reactions")
            var reactions: List<Reaction>

        ) : Chat()
    }
}
