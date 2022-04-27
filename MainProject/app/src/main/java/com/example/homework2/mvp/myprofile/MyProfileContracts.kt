package com.example.homework2.mvp.myprofile

import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Single

interface MyProfileView : BaseView {

    fun renderProfile(member: Member)

}

interface MyProfilePresenter : BasePresenter

interface MyProfileModel : BaseModel {

    fun getMyProfile(): Single<Member>

}
