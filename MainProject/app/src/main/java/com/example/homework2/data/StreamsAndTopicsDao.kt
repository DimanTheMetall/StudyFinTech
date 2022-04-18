package com.example.homework2.data

import android.annotation.SuppressLint
import androidx.room.*
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.streamsandtopics.Stream
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface StreamsAndTopicsDao {

    @SuppressLint("CheckResult")
    @Transaction
    fun insertStreams(streamsList: List<Stream>, type: String) {
        streamsList.forEach { stream ->
            insertTopicList(stream.topicList.map {
                TopicEntity.toEntity(
                    topic = it,
                    streamId = stream.stream_id
                )
            }).subscribe({}, {})
        }
        checkStreams(streams = streamsList.map {
            StreamEntity.toEntity(
                stream = it,
                type = type
            )
        })
    }

    fun insertStreamsAsynh(streamsList: List<Stream>, type: String): Completable {
        return Completable.create {
            insertStreams(streamsList, type)
            it.onComplete()
        }
    }

    @Transaction
    fun checkStreams(streams: List<StreamEntity>) {
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
                "ON streams.subscribedOrAll = 'subscribed'"
    )
    fun getSubscribedStreamsAndTopic(): Single<Map<StreamEntity, List<TopicEntity>>>

    @Query(
        "SELECT * FROM streams " +
                "INNER JOIN topics "
    )
    fun getAllStreamsAndTopic(): Single<Map<StreamEntity, List<TopicEntity>>>

    @Update
    fun updateStreamList(streamEntityList: List<StreamEntity>): Completable

    @Insert
    fun insertTopicList(topicEntityList: List<TopicEntity>): Completable

}
