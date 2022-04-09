package com.example.homework2.dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Topic(
    val name: String
): Parcelable
