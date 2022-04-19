package com.example.homework2.mvp.streams.recyclestream

import android.util.Log
import com.example.homework2.Constance
import com.example.homework2.data.ZulipDataBase
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class RecycleStreamsModelImpl(
    private val database: ZulipDataBase,
    private val retrofitService: RetrofitService
) : BaseModelImpl(), RecycleStreamModel {

    override fun insertStreamsAndTopics(streamsList: List<Stream>, isSubscribed: Boolean) {
        val entityType = if (isSubscribed) StreamEntity.SUBSCRIBED else StreamEntity.ALL

        val disposable =
            database.getStreamsAndTopicsDao().insertStreamsAsynh(streamsList, entityType)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { Log.d(Constance.Log.TOPIC_AND_STREAM, "INSERT SUCCESS") },
                    { Log.e(Constance.Log.TOPIC_AND_STREAM, it.toString()) })

        compositeDisposable.add(disposable)
    }

    override fun loadSubscribedStreams(): Single<List<Stream>> {
        return retrofitService.getSubscribedStreams()
            .delay(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it.streams) }
            .flatMap { stream ->
                retrofitService.getTopicList(stream.stream_id)
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
                retrofitService.getTopicList(stream.stream_id)
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