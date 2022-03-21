package com.example.homework2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChannelViewModel() : ViewModel() {

    private var _channelList = MutableLiveData<List<Channel>>().apply { value = listOf() }
    val channelList: LiveData<List<Channel>> = _channelList


    fun updateData(item: List<Channel>) {
        _channelList.value = item
    }
}


