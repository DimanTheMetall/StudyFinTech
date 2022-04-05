package com.example.homework2.dataclasses.chatdataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Website(
    val status: String,
    val timestamp: Int
): Parcelable
