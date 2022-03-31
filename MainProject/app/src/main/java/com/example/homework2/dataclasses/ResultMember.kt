package com.example.homework2.dataclasses

sealed class ResultMember {

    data class Success(val profileList: List<Member>) : ResultMember()

    object Progress: ResultMember()

    object Error: ResultMember()
}
