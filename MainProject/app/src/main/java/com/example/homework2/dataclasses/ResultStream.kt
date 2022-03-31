package com.example.homework2.dataclasses

sealed class ResultStream {

    data class Success(val streamList: List<StreamOrSubscribed.Stream>) : ResultStream()

    object Progress : ResultStream()

    object Error : ResultStream()
}
