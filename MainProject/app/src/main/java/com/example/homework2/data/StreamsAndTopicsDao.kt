package com.example.homework2.data

import androidx.room.*
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface StreamsAndTopicsDao {

    @Transaction
    fun insertStreams(streams: List<StreamEntity>) {
        streams.forEach { streamEntity ->
            val cashedStream = getStreamById(streamEntity.id.toLong())

            when {
                cashedStream == null -> {
                    insertStream(streamEntity)
                }
                cashedStream != streamEntity || streamEntity.subscribedOrAll == StreamEntity.SUBSCRIBED -> {
                    updateStream(streamEntity)
                }
            }
        }
    }

    @Update
    fun updateStream(stream: StreamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStream(stream: StreamEntity)

    @Query("SELECT * FROM streams WHERE id=:id")
    fun getStreamById(id: Long): StreamEntity?

    @Query(
        "SELECT * FROM streams " +
                "INNER JOIN topics " +
                "ON streams.id = topics.stream_id " +
                "AND streams.subscribedOrAll = 'subscribed'"
    )
    fun getSubscribedStreamsAndTopic(): Single<Map<StreamEntity, List<TopicEntity>>>

    @Query(
        "SELECT * FROM streams " +
                "INNER JOIN topics " +
                "ON streams.id = topics.stream_id "
    )
    fun getAllStreamsAndTopic(): Single<Map<StreamEntity, List<TopicEntity>>>

    @Update
    fun updateStreamList(streamEntityList: List<StreamEntity>): Completable

    @Insert
    fun insertTopicList(topicEntityList: List<TopicEntity>): Completable

}
