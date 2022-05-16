package com.example.homework2.dataclasses.streamsandtopics

import com.google.gson.annotations.SerializedName

class Subscriptions(
    @SerializedName("name")
    val name: String,

    //Используется при отправке на сервер
    @SerializedName("description")
    val description: String? = null
)
