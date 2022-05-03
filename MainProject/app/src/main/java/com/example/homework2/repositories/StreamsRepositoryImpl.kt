package com.example.homework2.repositories

import com.example.homework2.data.ZulipDataBase
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface StreamRepository {

    fun insertStreamsAndTopics(streamsList: List<Stream>, isSubscribed: Boolean): Completable

    fun loadSubscribedStreams(): Single<List<Stream>>

    fun loadAllStreams(): Single<List<Stream>>

    fun selectAllStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>>

    fun selectSubscribedStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>>
}


class StreamsRepositoryImpl @Inject constructor(
    private val retrofitService: RetrofitService,
    private val database: ZulipDataBase
) : StreamRepository {

    override fun insertStreamsAndTopics(
        streamsList: List<Stream>,
        isSubscribed: Boolean
    ): Completable {
        val entityType = if (isSubscribed) StreamEntity.SUBSCRIBED else StreamEntity.ALL
        return database.getStreamsAndTopicsDao().insertStreamsAsynh(streamsList, entityType)
            .subscribeOn(Schedulers.io())
    }


    override fun loadSubscribedStreams(): Single<List<Stream>> {
        return retrofitService.getSubscribedStreams()
            .delay(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it.streams) }
            .flatMap { stream ->
                retrofitService.getTopicList(stream.streamId)
                    .flatMapObservable { Observable.just(stream.copy(topicList = it.topics.toMutableList())) }
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadAllStreams(): Single<List<Stream>> {
        return retrofitService.getAllStreams()
            .delay(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it.streams) }
            .flatMap { stream ->
                retrofitService.getTopicList(stream.streamId)
                    .flatMapObservable { Observable.just(stream.copy(topicList = it.topics.toMutableList())) }
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun selectAllStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>> =
        database.getStreamsAndTopicsDao().getAllStreamsAndTopic()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    override fun selectSubscribedStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>> =
        database.getStreamsAndTopicsDao().getSubscribedStreamsAndTopic()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}