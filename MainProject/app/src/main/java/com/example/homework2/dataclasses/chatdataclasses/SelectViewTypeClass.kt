package com.example.homework2.dataclasses.chatdataclasses

import com.example.homework2.dataclasses.Reaction

sealed class SelectViewTypeClass {

    object Error : SelectViewTypeClass()

    object Progress : SelectViewTypeClass()

    data class Success(val messagesList: List<SelectViewTypeClass.Chat.Message>) : SelectViewTypeClass()

    sealed class Chat {

        data class Date(
            val time: String
        ) : SelectViewTypeClass.Chat()

        data class Message(
            val avatar_url: String,
            val client: String,
            val content: String,
            val content_type: String,
            val display_recipient: Any,
            val flags: List<String>,
            val id: Int,
            val is_me_message: Boolean,
            val reactions: List<Any>,
            val recipient_id: Int,
            val sender_email: String,
            val sender_full_name: String,
            val sender_id: Int,
            val sender_realm_str: String,
            val stream_id: Int,
            val subject: String,
            val submessages: List<Any>,
            val timestamp: Int,
            val topic_links: List<Any>,
            val type: String,
            val emojiList: List<Reaction> = listOf()

        ): SelectViewTypeClass.Chat()
    }

}
