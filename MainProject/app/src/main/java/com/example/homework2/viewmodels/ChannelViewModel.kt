package com.example.homework2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.homework2.dataclasses.Channel
import com.example.homework2.dataclasses.ChannelTopic

class ChannelViewModel() : ViewModel() {

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


    fun updateAllChanelData(itemList: List<Channel>) {
        _allChannelList.value = itemList
    }

    fun updateSubscribedChanelData(itemList: List<Channel>) {
        _subscribedList.value = itemList
    }
}



