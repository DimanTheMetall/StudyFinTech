package com.example.homework2.dataclasses

sealed class Result {

    data class Success(val channelList: List<Channel>) : Result()

    object Progress : Result()

    object Error : Result()
}