package com.example.homework2.dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(
    val avatar_url: String,
    val bot_owner_id: Int,
//    val bot_type: Any,
    val date_joined: String,
    val email: String,
    val full_name: String,
    val is_active: Boolean,
    val is_admin: Boolean,
    val is_billing_admin: Boolean,
    val is_bot: Boolean,
    val is_guest: Boolean,
    val is_owner: Boolean,
//    val profile_data: ProfileData,
    val role: Int,
    val timezone: String,
    val user_id: Int,
    val msg: String = ""
) : Parcelable