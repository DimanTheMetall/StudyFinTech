package com.example.homework2.dataclasses.streamsandtopics

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stream(
    @SerializedName("stream_id")
    val streamId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("topicList")
    var topicList: MutableList<Topic> = mutableListOf(),
): Parcelable
