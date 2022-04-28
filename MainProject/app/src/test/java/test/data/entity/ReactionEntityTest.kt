package test.data.entity

import com.example.homework2.data.local.entity.ReactionEntity
import com.example.homework2.dataclasses.chatdataclasses.Reaction
import org.junit.Assert.assertEquals
import org.junit.Test

class ReactionEntityTest(

) {

    @Test
    fun `response data with out userId to entity`() {
        val taskResponse = createReactionClassWithOutUserId()

        val testTask = ReactionEntity.toEntity(reaction = taskResponse, messageId = 0L)

        assertEquals(0, testTask.userId)
    }

    @Test
    fun `response full data to entity`() {
        val taskResponse =
            createReactionClassWithUserId(
                emojiCode = "4ffD",
                emojiName = "name44f",
                userId = 2994,
                reactionType = "reation44f",
            )

        val testTask = ReactionEntity.toEntity(taskResponse, 4444L)

        assertEquals("4ffD", testTask.emojiCode)
        assertEquals("name44f", testTask.emojiName)
        assertEquals(2994, testTask.userId)
        assertEquals("reation44f", testTask.reactionType)
        assertEquals(4444L, testTask.messageId)
    }

    @Test
    fun `to domain class response full data`() {
        val taskResponse = createEntity(
            emojiCode = "emoji code",
            emojiName = "emoji name",
            userId = 2,
            reactionType = "reaction type",
            messageId = 3L
        )

        val testTask = taskResponse.toReaction()

        assertEquals("emoji code", testTask.emojiCode)
        assertEquals("emoji name", testTask.emojiName)
        assertEquals(2, testTask.userId)
        assertEquals("reaction type", testTask.reactionType)
    }

    @Test
    fun `to domain and then to entity`() {
        val taskResponse = createEntity(
            emojiCode = "emoji code",
            emojiName = "emoji name",
            userId = 100,
            reactionType = "reaction type",
            messageId = 101L
        )
        val testTask = ReactionEntity.toEntity(taskResponse.toReaction(), taskResponse.messageId)

        assertEquals(taskResponse.emojiCode, testTask.emojiCode)
        assertEquals(taskResponse.emojiName, testTask.emojiName)
        assertEquals(taskResponse.messageId, testTask.messageId)
        assertEquals(taskResponse.reactionType, testTask.reactionType)
        assertEquals(taskResponse.userId, testTask.userId)
    }

    private fun createEntity(
        emojiCode: String = "",
        emojiName: String = "",
        userId: Int = 0,
        reactionType: String = "",
        messageId: Long = 0L
    ): ReactionEntity {
        return ReactionEntity(
            emojiCode = emojiCode,
            emojiName = emojiName,
            userId = userId,
            reactionType = reactionType,
            messageId = messageId
        )
    }

    private fun createReactionClassWithOutUserId(
        emojiCode: String = "",
        emojiName: String = "",
        reactionType: String = "",
    ): Reaction {
        return Reaction(
            emojiCode = emojiCode,
            emojiName = emojiName,
            reactionType = reactionType,
        )
    }

    private fun createReactionClassWithUserId(
        emojiCode: String = "",
        emojiName: String = "",
        userId: Int = 0,
        reactionType: String = "",
    ): Reaction {
        return Reaction(
            emojiCode = emojiCode, emojiName = emojiName,
            userId = userId, reactionType = reactionType
        )
    }

}
