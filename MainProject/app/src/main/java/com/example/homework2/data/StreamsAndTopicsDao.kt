package com.example.homework2.data

import android.annotation.SuppressLint
import android.util.Log
import androidx.room.*
import com.example.homework2.Constance
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
            val streamEntity = StreamEntity.toEntity(stream = stream, type = type)
            val cashedStream = getStreamById(streamEntity.id.toLong())

            when {
                cashedStream == null -> {
                    insertStream(streamEntity)

                }
                cashedStream != streamEntity || streamEntity.subscribedOrAll == StreamEntity.SUBSCRIBED -> {
                    updateStream(streamEntity)
                }
            }
            insertTopicList(stream.topicList.map {
                TopicEntity.toEntity(
                    topic = it,
                    streamId = stream.stream_id
                )
            }).subscribe({}, { Log.e(Constance.LogTag.TOPIC_AND_STREAM, "$it") })
        }

    }

    fun insertStreamsAsynh(streamsList: List<Stream>, type: String): Completable {
        return Completable.create {
            insertStreams(streamsList, type)
            it.onComplete()
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
                "LEFT JOIN topics " +
                "ON streams.subscribedOrAll = 'subscribed' " +
                "WHERE streams.id = topics.stream_id"
    )
    fun getSubscribedStreamsAndTopic(): Single<Map<StreamEntity, List<TopicEntity>>>

    @Query(
        "SELECT * FROM streams " +
                "LEFT JOIN topics " +
                "WHERE streams.id = topics.stream_id"
    )
    fun getAllStreamsAndTopic(): Single<Map<StreamEntity, List<TopicEntity>>>

    @Update
    fun updateStreamList(streamEntityList: List<StreamEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopicList(topicEntityList: List<TopicEntity>): Completable

}
