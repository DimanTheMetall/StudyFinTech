package com.example.homework2.mvp.streams.recyclestream

import com.example.homework2.data.ZulipDataBase
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class RecycleStreamsModelImpl(
    private val database: ZulipDataBase,
    private val retrofitService: RetrofitService
) : BaseModelImpl(), RecycleStreamModel {


    //Пофиксить
    override fun insertStreamsANdTopics(streamsList: List<Stream>, isSubscribed: Boolean) {
        val entityType = if (isSubscribed) StreamEntity.SUBSCRIBED else StreamEntity.ALL

        val streamDisposable = database.getStreamsAndTopicsDao()
            .insertStreams(streams = streamsList.map {
                StreamEntity.toEntity(
                    stream = it,
                    type = entityType
                )
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {}

        streamsList.forEach { stream ->

            val topicsDisposable =
                database.getStreamsAndTopicsDao().insertTopicList(stream.topicList.map {
                    TopicEntity.toEntity(
                        topic = it,
                        streamId = stream.stream_id
                    )
                })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({}, {})
            compositeDisposable.add(topicsDisposable)
        }
        compositeDisposable.add(streamDisposable)
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

    override fun selectAllStreamsAndTopics(): Flowable<Map<StreamEntity, List<TopicEntity>>> =
        database.getStreamsAndTopicsDao().getAllStreamsAndTopic()
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())


    override fun selectSubscribedStreamsAndTopics(): Flowable<Map<StreamEntity, List<TopicEntity>>> =
        database.getStreamsAndTopicsDao().getSubscribedStreamsAndTopic()
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())

}