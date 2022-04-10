package com.example.homework2.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.homework2.data.ZulipDataBase
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.ResultStream
import com.example.homework2.dataclasses.Stream
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class StreamViewModel : ViewModel() {

    lateinit var dataBase: ZulipDataBase

    private val compositeDisposable = CompositeDisposable()
    private val allChannelList = mutableListOf<Stream>()
    private var subscribedList = mutableListOf<Stream>()

    val allChannelsObservable: Observable<ResultStream>
        get() = allChannelsSubject
    private val allChannelsSubject =
        BehaviorSubject.create<ResultStream>().apply {
            onNext(ResultStream.Success(allChannelList)) //Затычка
        }

    val subscribedChannelsObservable: Observable<ResultStream>
        get() = subscribedChannelsSubject
    private val subscribedChannelsSubject = BehaviorSubject.create<ResultStream>().apply {
        onNext(ResultStream.Success(subscribedList)) //Затычка
    }


    fun initDataBase(context: Context) {
        dataBase = ZulipDataBase.getInstance(context)
        selectSubscribedStreamsAndTopics()
        selectAllStreamsAndTopics()
    }

    fun insertStreamsAndTopics(streamsList: List<Stream>, isSubscribed: Boolean) {

        val entityType = if (isSubscribed) StreamEntity.SUBSCRIBED else StreamEntity.ALL

        val streamDisposable = dataBase.getStreamsAndTopicsDao()
            .insertStreams(streams = streamsList.map { StreamEntity.toEntity(it, entityType) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {})

        streamsList.forEach { stream ->

            val topicsDisposable =
                dataBase.getStreamsAndTopicsDao().insertTopicList(stream.topicList.map {
                    TopicEntity.toEntity(
                        topic = it,
                        streamId = stream.stream_id
                    )
                })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({}, {})

            compositeDisposable.add(streamDisposable)
            compositeDisposable.add(topicsDisposable)
        }
    }


    private fun selectSubscribedStreamsAndTopics() {
        val disposable = dataBase.getStreamsAndTopicsDao().getSubscribedStreamsAndTopic()
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { map ->
                val streamList = mutableListOf<Stream>()
                map.keys.forEach { streamEntity ->
                    val stream = streamEntity.toStream()
                    val topicList = map.getValue(streamEntity).map { it.toTopic() }
                    stream.topicList = topicList.toMutableList()
                    streamList.add(stream)
                }
                subscribedChannelsSubject.onNext(ResultStream.Success(streamList = streamList.sortedBy { it.stream_id }))
            }


        compositeDisposable.add(disposable)
    }

    private fun selectAllStreamsAndTopics() {

        val disposable = dataBase.getStreamsAndTopicsDao().getAllStreamsAndTopic()
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { map ->

                val streamList = mutableListOf<Stream>()
                map.keys.forEach { streamEntity ->
                    val stream = streamEntity.toStream()
                    val topicList = map.getValue(streamEntity).map { it.toTopic() }
                    stream.topicList = topicList.toMutableList()
                    streamList.add(stream)
                }
                allChannelsSubject.onNext(ResultStream.Success(streamList = streamList.sortedBy { it.stream_id }))
            }
        compositeDisposable.add(disposable)
    }

    fun onSearchChangedSubscribedChannel(searchText: String, retrofitService: RetrofitService) {
        subscribedChannelsSubject.onNext(ResultStream.Progress)
        val disposableSub = retrofitService.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it.streams) }
            .flatMap { stream ->
                retrofitService.getTopicList(stream.stream_id)
                    .flatMapObservable { Observable.just(stream.copy(topicList = it.topics.toMutableList())) }
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stream ->
                subscribedChannelsSubject.onNext(ResultStream.Success(
                    stream.filter { it.name.contains(searchText) }
                ))
            }, {
                subscribedChannelsSubject.onNext(ResultStream.Error)
            })
        compositeDisposable.add(disposableSub)
    }

    fun onSearchChangedAllChannel(searchText: String, retrofitService: RetrofitService) {
        allChannelsSubject.onNext(ResultStream.Progress)
        val disposableAll = retrofitService.getAllStreams()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it.streams) }
            .flatMap { stream ->
                retrofitService.getTopicList(stream.stream_id)
                    .flatMapObservable { Observable.just(stream.copy(topicList = it.topics.toMutableList())) }
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stream ->
                allChannelsSubject.onNext(ResultStream.Success(
                    stream.filter { it.name.contains(searchText) }
                ))
            }, {
                allChannelsSubject.onNext(ResultStream.Error)
            })
        compositeDisposable.add(disposableAll)
    }

    fun loadSubscribedStreams(retrofitService: RetrofitService) {
        subscribedChannelsSubject.onNext(ResultStream.Progress)
        val streamsDisposable = retrofitService.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it.streams) }
            .flatMap { stream ->
                retrofitService.getTopicList(stream.stream_id)
                    .flatMapObservable { Observable.just(stream.copy(topicList = it.topics.toMutableList())) }
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                insertStreamsAndTopics(it, true)
            }, {
                println(it)
                subscribedChannelsSubject.onNext(ResultStream.Error)
            })

        compositeDisposable.add(streamsDisposable)
    }

    fun loadAllStreams(retrofitService: RetrofitService) {
        allChannelsSubject.onNext(ResultStream.Progress)
        val streamsDisposable = retrofitService.getAllStreams()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it.streams) }
            .flatMap { stream ->
                retrofitService.getTopicList(stream.stream_id)
                    .flatMapObservable { Observable.just(stream.copy(topicList = it.topics.toMutableList())) }
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                insertStreamsAndTopics(it, false)
            }, {
                println(it)
                allChannelsSubject.onNext(ResultStream.Error)
            })

        compositeDisposable.add(streamsDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}
