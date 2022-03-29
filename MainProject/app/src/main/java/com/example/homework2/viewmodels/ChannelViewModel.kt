package com.example.homework2.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Channel
import com.example.homework2.dataclasses.ChannelTopic
import com.example.homework2.dataclasses.ResultChannel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ChannelViewModel() : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val allChannelList = listOf(
        Channel(
            "name 1", mutableListOf(
                ChannelTopic("topikName 1", 24),
                ChannelTopic("topikName 2", 1),
                ChannelTopic("topikName 3", 24),
                ChannelTopic("topikName 4", 13),
                ChannelTopic("topikName 5", 2),
            )
        ),
        Channel(
            "name 2", mutableListOf(
                ChannelTopic("topikName 1", 24),
                ChannelTopic("topikName 2", 1),
                ChannelTopic("topikName 3", 24),
                ChannelTopic("topikName 4", 13),
                ChannelTopic("topikName 5", 2),
            )
        ),
        Channel(
            "name 3", mutableListOf(
                ChannelTopic("topikName 1", 24),
                ChannelTopic("topikName 2", 1),
                ChannelTopic("topikName 3", 24),
                ChannelTopic("topikName 4", 13),
                ChannelTopic("topikName 5", 2),
            )
        )
    )

    private var subscribedList = (
            listOf(
                Channel(
                    "subname 1", mutableListOf(
                        ChannelTopic("subtopikName 1", 24),
                        ChannelTopic("subtopikName 2", 1),
                        ChannelTopic("subtopikName 3", 24),
                        ChannelTopic("subtopikName 4", 13),
                        ChannelTopic("subtopikName 5", 2),
                    )
                ),
                Channel(
                    "subname 2", mutableListOf(
                        ChannelTopic("subtopikName 1", 24),
                        ChannelTopic("subtopikName 2", 1),
                        ChannelTopic("subtopikName 3", 24),
                        ChannelTopic("subtopikName 4", 13),
                        ChannelTopic("subtopikName 5", 2),
                    )
                )
            ))

    val allChannelsObservable: Observable<ResultChannel>
        get() = allChannelsSubject
    private val allChannelsSubject =
        BehaviorSubject.create<ResultChannel>().apply {
            onNext(ResultChannel.Success(allChannelList)) //Затычка
        }

    val subscribedChannelsObservable: Observable<ResultChannel>
        get() = subscribedChannelsSubject
    private val subscribedChannelsSubject = BehaviorSubject.create<ResultChannel>().apply {
        onNext(ResultChannel.Success(subscribedList)) //Затычка
    }

    fun onSearchChangedSubscribedChannel(searchText: String) {
        subscribedChannelsSubject.onNext(ResultChannel.Progress)
        val disposableSub = Observable.timer(1, TimeUnit.SECONDS) //Эмуляция запроса в сеть
            .subscribeOn(Schedulers.io())
            .subscribe {
                val error = Random.nextBoolean() //Эмуляция ошибки
                if (!error) {
                    subscribedChannelsSubject.onNext(
                        ResultChannel.Success(
                            subscribedList.filter {
                                it.name.contains(
                                    searchText
                                )
                            })
                    )
                } else {
                    subscribedChannelsSubject.onNext(ResultChannel.Error)
                }
            }
        compositeDisposable.add(disposableSub)
    }

    fun onSearchChangedAllChannel(searchText: String) {
        allChannelsSubject.onNext(ResultChannel.Progress)
        val disposableAll = Observable.timer(1, TimeUnit.SECONDS) //Эмуляция запроса в сеть
            .subscribeOn(Schedulers.io())
            .subscribe {
                val error = Random.nextBoolean()
                if (!error) {
                    allChannelsSubject.onNext(
                        ResultChannel.Success(
                            allChannelList.filter {
                                it.name.contains(
                                    searchText
                                )
                            })
                    )
                } else {
                    allChannelsSubject.onNext(ResultChannel.Error)
                }
            }
        compositeDisposable.add(disposableAll)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}
