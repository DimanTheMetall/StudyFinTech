package com.example.homework2.dataclasses

//Не известно понадобиться ли это класс в дальнейшем
data class Subscription(
    val audible_notifications: Boolean,
    val color: String,
    val description: String,
    val desktop_notifications: Boolean,
    val email_address: String,
    val invite_only: Boolean,
    val is_muted: Boolean,
    val name: String,
    val pin_to_top: Boolean,
    val push_notifications: Boolean,
    val role: Int,
    val stream_id: Int,
    val subscribers: List<Int>
)
