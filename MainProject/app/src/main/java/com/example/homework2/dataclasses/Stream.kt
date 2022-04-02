package com.example.homework2.dataclasses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stream(
    val stream_id: Int,
    val invite_only: Boolean,
    val name: String,
    var topicList: MutableList<Topic> = mutableListOf(),
    val role: Int?=null,
    var subscribers: List<Int>? = null
): Parcelable


