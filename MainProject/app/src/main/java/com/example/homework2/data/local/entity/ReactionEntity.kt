package com.example.homework2.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.homework2.dataclasses.chatdataclasses.Reaction

@Entity(
    tableName = "reactions",
    primaryKeys = ["message_id", "emoji_name", "user_id"],
    foreignKeys = [ForeignKey(
        entity = MessageEntity::class,
        parentColumns = ["id"],
        childColumns = ["message_id"],
        onUpdate = ForeignKey.CASCADE,
    )]
)
data class ReactionEntity(
    @ColumnInfo(name = "emoji_code")
    val emojiCode: String,

    @ColumnInfo(name = "emoji_name")
    val emojiName: String,

    @ColumnInfo(name = "user_id")
    val userId: Int = 0,

    @ColumnInfo(name = "reaction_type")
    val reactionType: String,

    @ColumnInfo(name = "message_id")
    val messageId: Long
) {
    fun toReaction(): Reaction = Reaction(
        emoji_code = emojiCode,
        emoji_name = emojiName,
        user_id = userId,
        reaction_type = reactionType
    )

    companion object {
        fun toEntity(reaction: Reaction, messageId: Long): ReactionEntity = ReactionEntity(
            emojiCode = reaction.emoji_code,
            emojiName = reaction.emoji_name,
            userId = reaction.user_id,
            reactionType = reaction.reaction_type,
            messageId = messageId
        )
    }
}
