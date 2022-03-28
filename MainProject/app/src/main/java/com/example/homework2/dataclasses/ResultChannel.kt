package com.example.homework2.dataclasses

sealed class ResultChannel {

    data class Success(val channelList: List<Channel>) : ResultChannel()

    object Progress : ResultChannel()

    object Error : ResultChannel()
}
