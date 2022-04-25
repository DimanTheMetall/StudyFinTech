package com.example.homework2.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.homework2.dataclasses.chatdataclasses.SelectViewTypeClass

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["stream_id", "name"],
            childColumns = ["stream_id", "subject"]
        )
    ]
)
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = "",

    @ColumnInfo(name = "client")
    val client: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "content_type")
    val contentType: String,

    @ColumnInfo(name = "sender_email")
    val senderEmail: String,

    @ColumnInfo(name = "sender_full_name")
    val senderFullName: String,

    @ColumnInfo(name = "sender_id")
    val senderId: Int,

    @ColumnInfo(name = "stream_id")
    val streamId: Int,

    @ColumnInfo(name = "subject")
    val subject: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "type")
    val type: String,

) {
    fun toMessage(): SelectViewTypeClass.Chat.Message = SelectViewTypeClass.Chat.Message(
        id = id,
        avatarUrl = avatarUrl ?: "",
        client = client,
        content = content,
        contentType = contentType,
        senderEmail = senderEmail,
        senderFullName = senderFullName,
        senderId = senderId,
        streamId = streamId,
        subject = subject,
        timestamp = timestamp,
        type = type,
        reactions = mutableListOf()
    )

    companion object {
        fun toEntity(message: SelectViewTypeClass.Chat.Message): MessageEntity = MessageEntity(
            id = message.id,
            avatarUrl = message.avatarUrl,
            client = message.client,
            content = message.content,
            contentType = message.contentType,
            senderEmail = message.senderEmail,
            senderFullName = message.senderFullName,
            senderId = message.senderId,
            streamId = message.streamId,
            subject = message.subject,
            timestamp = message.timestamp,
            type = message.type,

            )
    }
}
