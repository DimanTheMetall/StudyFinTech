package com.example.homework2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.homework2.Profile

class ProfileViewModel() : ViewModel() {

    private var _profileList = MutableLiveData<List<Profile>>().apply {
        value = listOf(
            Profile("Name 1", 1, "emailemail1@email.email", true),
            Profile("Name 2", 1, "emailemail2@email.email", true),
            Profile("Name 3", 1, "emailemail3@email.email", false),
            Profile("Name 4", 1, "emailemail4@email.email", true),
            Profile("Name 5", 1, "emailemail5@email.email", false),
            Profile("Name 6", 1, "emailemail6@email.email", true)
        )
    }
    val profileList: LiveData<List<Profile>> = _profileList

    fun updateData(itemList: List<Profile>) {
        _profileList.value = itemList
    }

}

