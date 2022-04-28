package com.example.homework2.mvp.otherprofile

import com.example.homework2.dataclasses.chatdataclasses.Presence
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.repositories.UsersRepository
import io.reactivex.Single
import javax.inject.Inject

class OtherProfileModelImpl @Inject constructor(
    private val usersRepositoryImpl: UsersRepository
) : BaseModelImpl(), OtherProfileModel {

    override fun getPresence(member: Member): Single<Presence> {
        return usersRepositoryImpl.loadPresence(member = member)
    }

}
