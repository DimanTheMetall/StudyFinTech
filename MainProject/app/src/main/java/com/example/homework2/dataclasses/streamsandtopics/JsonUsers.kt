package com.example.homework2.dataclasses.streamsandtopics

import com.google.gson.annotations.SerializedName

class JsonUsers(

    @SerializedName("members")
    val members: List<Member>,

    @SerializedName("result")
    val result: String
)
