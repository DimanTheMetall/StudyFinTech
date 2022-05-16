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
    fun insertOrReplaceMessagesFromTopic(listMessagesEntity: List<MessageEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceAllReactionOnMessages(reactionEntityList: List<ReactionEntity>): Completable

    @Query("DELETE FROM messages WHERE messages.stream_id=:streamId AND messages.subject=:topicName")
    fun deleteAllMessagesFromTopic(streamId: Int, topicName: String): Completable

    @Query(
        "DELETE FROM messages " +
                "WHERE messages.id<=:oldestMessagedItAfterDeleted " +
                "AND messages.stream_id=:streamId " +
                "AND messages.subject =:topicName"
    )
    fun deleteOldestMessages(
        oldestMessagedItAfterDeleted: Long,
        streamId: Int,
        topicName: String
    ): Completable

    @Delete
    fun deleteMessage(messageEntity: MessageEntity): Completable

    @Update
    fun updateMessage(messageEntity: MessageEntity): Completable

    @Insert
    fun insertMessage(messageEntity: MessageEntity): Completable
}
