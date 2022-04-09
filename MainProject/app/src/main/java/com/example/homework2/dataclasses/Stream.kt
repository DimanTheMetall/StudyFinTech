package com.example.homework2.dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stream(
    val stream_id: Int,
    val name: String,
    var topicList: MutableList<Topic> = mutableListOf(),
): Parcelable
