package com.example.homework2.data

import androidx.room.*
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Dao
interface StreamsAndTopicsDao {

    @Transaction
    fun insertStreams(streams: List<StreamEntity>): Completable {
        return Completable.create { emitter ->
            streams.forEach { streamEntity ->
                var cashedStream: StreamEntity?

                val disposable = getStreamById(streamEntity.id.toLong())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        cashedStream = streamEntity
                        when {
                            cashedStream == null -> {
                                insertStream(streamEntity)
                            }
                            cashedStream != streamEntity || streamEntity.subscribedOrAll == StreamEntity.SUBSCRIBED -> {
                                updateStream(streamEntity)
                            }
                        }
                    }, {})
                disposable.dispose()
            }
            emitter.onComplete()
        }
    }

    @Update
    fun updateStream(stream: StreamEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStream(stream: StreamEntity): Completable

    @Query("SELECT * FROM streams WHERE id=:id")
    fun getStreamById(id: Long): Single<StreamEntity?>

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
