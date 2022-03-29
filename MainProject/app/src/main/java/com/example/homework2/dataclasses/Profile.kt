package com.example.homework2.dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profile(
    val name: String,
    val imageId: Int,
    val email: String,
    var isOnline: Boolean = false
) : Parcelable
