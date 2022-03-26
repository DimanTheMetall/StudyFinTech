package com.example.homework2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Channel
import com.example.homework2.dataclasses.ChannelTopic
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.lang.RuntimeException
import java.util.*
import kotlin.random.Random

class ChannelViewModel() : ViewModel() {

    val channelList = listOf(
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


    private var _allChannelList = MutableLiveData<List<Channel>>().apply {
        value = listOf(
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
                "name 1", mutableListOf(
                    ChannelTopic("topikName 1", 24),
                    ChannelTopic("topikName 2", 1),
                    ChannelTopic("topikName 3", 24),
                    ChannelTopic("topikName 4", 13),
                    ChannelTopic("topikName 5", 2),
                )
            ),
            Channel(
                "name 1", mutableListOf(
                    ChannelTopic("topikName 1", 24),
                    ChannelTopic("topikName 2", 1),
                    ChannelTopic("topikName 3", 24),
                    ChannelTopic("topikName 4", 13),
                    ChannelTopic("topikName 5", 2),
                )
            )
        )
    }
    val allChannelList: LiveData<List<Channel>> = _allChannelList

    private var _subscribedList = MutableLiveData<List<Channel>>().apply {
        value = (
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
    }
    val subscribedList: LiveData<List<Channel>> = _subscribedList

    val allChannelsObservable: Observable<List<Channel>> get() = allChannelsSubject
    private val allChannelsSubject = BehaviorSubject.create<List<Channel>>().apply {
        onNext(channelList) //Затычка
    }

    fun updateAllChanelData(itemList: List<Channel>) {
        _allChannelList.value = itemList
    }

    fun updateSubscribedChanelData(itemList: List<Channel>) {
        _subscribedList.value = itemList
    }

    fun onSearchChanged(searchText: String) {
        val error = Random.nextBoolean()
        if (!error) {
            allChannelsSubject.onNext(channelList.filter { it.name.contains(searchText) })
        } else {
            allChannelsSubject.onError(RuntimeException("ERROR"))
        }
    }


}



