package com.example.homework2.dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Topic(
    var max_id: Int,
    val name: String
): Parcelable
