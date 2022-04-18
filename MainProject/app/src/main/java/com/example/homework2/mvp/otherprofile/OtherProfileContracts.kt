package com.example.homework2.mvp.otherprofile

import com.example.homework2.dataclasses.chatdataclasses.Presence
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Single

interface OtherProfileView : BaseView {

    fun updateUser()

    fun setStatus(member: Member, presence: Presence)
}

interface OtherProfilePresenter : BasePresenter {

    fun onUserNeededUpdate(member: Member)

}

interface OtherProfileModel : BaseModel {

    fun loadPresence(member: Member): Single<Presence>
}