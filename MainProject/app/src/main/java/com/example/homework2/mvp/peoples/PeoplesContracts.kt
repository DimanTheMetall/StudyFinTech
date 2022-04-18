package com.example.homework2.mvp.peoples

import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Single

interface PeoplesView : BaseView {

    fun showProgress()

    fun showError()

    fun showUsers(userList: List<Member>)
}


interface PeoplesPresenter : BasePresenter {

    fun onSearchedTextChanged(searchedText: String)
}


interface PeoplesModel : BaseModel {

    fun loadAllUsersWithOutPresence(): Single<List<Member>>

    fun loadAllUsersWithPresence(): Single<List<Member>>
}