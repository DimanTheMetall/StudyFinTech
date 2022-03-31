package com.example.homework2.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.ResultStream
import com.example.homework2.dataclasses.Stream
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class StreamViewModel : ViewModel() {




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

    fun onSearchChangedSubscribedChannel(searchText: String) {
        subscribedChannelsSubject.onNext(ResultStream.Progress)
        val disposableSub = Observable.timer(1, TimeUnit.SECONDS) //Эмуляция запроса в сеть
            .subscribeOn(Schedulers.io())
            .subscribe {
                val error = Random.nextBoolean() //Эмуляция ошибки
                if (!error) {
                    subscribedChannelsSubject.onNext(
                        ResultStream.Success(
                            subscribedList.filter {
                                it.name.contains(
                                    searchText
                                )
                            })
                    )
                } else {
                    subscribedChannelsSubject.onNext(ResultStream.Error)
                }
            }
        compositeDisposable.add(disposableSub)
    }

    fun onSearchChangedAllChannel(searchText: String) {
        allChannelsSubject.onNext(ResultStream.Progress)
        val disposableAll = Observable.timer(1, TimeUnit.SECONDS) //Эмуляция запроса в сеть
            .subscribeOn(Schedulers.io())
            .subscribe {
                val error = Random.nextBoolean()
                if (!error) {
                    allChannelsSubject.onNext(
                        ResultStream.Success(
                            allChannelList.filter {
                                it.name.contains(
                                    searchText
                                )
                            })
                    )
                } else {
                    allChannelsSubject.onNext(ResultStream.Error)
                }
            }
        compositeDisposable.add(disposableAll)
    }

    fun loadSubscribedStreams(retrofitService: RetrofitService){
        subscribedChannelsSubject.onNext(ResultStream.Progress)
        val streamDisposable = retrofitService.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({},{})


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
                allChannelsSubject.onNext(ResultStream.Success(it))
            }, {

            })
        compositeDisposable.add(streamsDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}
