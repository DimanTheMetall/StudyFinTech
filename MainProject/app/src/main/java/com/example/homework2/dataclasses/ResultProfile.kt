package com.example.homework2.dataclasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class ResultProfile {

    data class Success(val profileList: List<Profile>) : ResultProfile()

    object Progress: ResultProfile()

    object Error: ResultProfile()
}
