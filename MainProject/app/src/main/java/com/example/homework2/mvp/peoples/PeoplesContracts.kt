package com.example.homework2.mvp.peoples

import com.example.homework2.Errors
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Single

interface PeoplesView : BaseView {

    fun openProfileFragment(member: Member)

    fun showProgress()

    fun showError(throwable: Throwable, error: Errors)

    fun showUsers(userList: List<Member>)

}


interface PeoplesPresenter : BasePresenter {

    fun onProfileCLicked(member: Member)

    fun onSearchedTextChanged(searchedText: String)

    fun onStart()

}


interface PeoplesModel : BaseModel {

    fun getUsersWithOutPresence(): Single<List<Member>>

    fun getAllUsersWithPresence(): Single<List<Member>>

}
