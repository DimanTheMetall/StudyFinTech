package com.example.homework2.dataclasses

sealed class ResultProfile {

    data class Success(val profileList: List<Profile>) : ResultProfile()

    object Progress: ResultProfile()

    object Error: ResultProfile()
}
