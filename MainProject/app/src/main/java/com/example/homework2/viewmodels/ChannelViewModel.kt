package com.example.homework2.viewmodels

import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Channel
import com.example.homework2.dataclasses.ChannelTopic
import com.example.homework2.dataclasses.Result
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

    val allChannelsObservable: Observable<com.example.homework2.dataclasses.Result> get() = allChannelsSubject
    private val allChannelsSubject = BehaviorSubject.create<com.example.homework2.dataclasses.Result>().apply {
        onNext(Result.Success(allChannelList)) //Затычка
    }

    val subscribedChannelsObservable: Observable<List<Channel>>
        get() = subscribedChannelsSubject
    private val subscribedChannelsSubject = BehaviorSubject.create<List<Channel>>().apply {

        onNext(subscribedList) //Затычка
    }

    fun onSearchChanged(searchText: String) {
        allChannelsSubject.onNext(com.example.homework2.dataclasses.Result.Progress)
        val d = Observable.timer(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {
                val error = Random.nextBoolean()
                if (!error) {
                    allChannelsSubject.onNext(com.example.homework2.dataclasses.Result.Success(allChannelList.filter {
                        it.name.contains(
                            searchText
                        )
                    }))
                } else {
                    allChannelsSubject.onNext(com.example.homework2.dataclasses.Result.Error)
                }
            }
        compositeDisposable.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}
