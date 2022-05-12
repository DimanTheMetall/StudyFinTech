package com.example.homework2.data

import androidx.room.*
import com.example.homework2.data.local.entity.MessageEntity
import com.example.homework2.data.local.entity.ReactionEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessagesAndReactionDao {

    @Query(
        "SELECT * FROM messages LEFT JOIN reactions " +
                "ON messages.id = reactions.message_id " +
                "WHERE messages.subject = :topicName AND messages.stream_id = :streamId"
    )
    fun selectMessagesAndReactionFromTopic(
        topicName: String,
        streamId: Int
    ): Single<Map<MessageEntity, List<ReactionEntity>>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessagesFromTopic(listMessagesEntity: List<MessageEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReactionOnMessages(reactionEntityList: List<ReactionEntity>): Completable

    @Query(
        "DELETE FROM messages " +
                "WHERE messages.id<:oldestMessagedItAfterDeleted " +
                "AND messages.stream_id=:streamId " +
                "AND messages.subject =:topicName"
    )
    fun deleteOldestMessages(
        oldestMessagedItAfterDeleted: Long,
        streamId: Int,
        topicName: String
    ): Completable

    @Query("DELETE FROM reactions WHERE reactions.message_id<:messageId")
    fun deleteReactionFromMessagesWhereIdLowes(messageId: Long): Completable

    @Delete
    fun deleteMessage(messageEntity: MessageEntity): Completable
}
