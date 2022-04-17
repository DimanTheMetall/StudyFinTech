package com.example.homework2.dataclasses.streamsandtopics

import android.os.Parcelable
import com.example.homework2.dataclasses.chatdataclasses.Website
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(
    @SerializedName("avatar_url")
    val avatarUrl: String?,

    @SerializedName("email")
    val email: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("is_owner")
    val isOwner: Boolean,

    @SerializedName("role")
    val role: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("msg")
    val msg: String? = "",

    @SerializedName("website")
    val website: Website?
) : Parcelable
