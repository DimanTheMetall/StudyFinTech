package com.example.homework2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.homework2.customviews.Reaction
import com.example.homework2.customviews.SelectViewTypeClass

class ChatViewModel(): ViewModel() {
    private val _chatList = MutableLiveData<List<SelectViewTypeClass>>().apply {
        value = listOf(
            SelectViewTypeClass.Message (1,
                "textMessage",
                "titleMessage",
                2,
                false,
                emptyList<Reaction>().toMutableList()),
            SelectViewTypeClass.Message (2,
                "textMessage",
                "titleMessage",
                2,
                true,
                emptyList<Reaction>().toMutableList()),
            SelectViewTypeClass.Date("time 02")
        )
    }
    val chatList: LiveData<List<SelectViewTypeClass>> = _chatList

    fun updateListData(itemList: List<SelectViewTypeClass>){
        _chatList.value = itemList
    }


}