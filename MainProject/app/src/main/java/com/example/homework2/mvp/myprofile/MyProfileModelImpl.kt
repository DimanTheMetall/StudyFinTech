package com.example.homework2.mvp.myprofile

import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.repositories.UsersRepository
import io.reactivex.Single
import javax.inject.Inject

class MyProfileModelImpl @Inject constructor(
    private val usersRepository: UsersRepository
) : BaseModelImpl(), MyProfileModel {

    override fun getMyProfile(): Single<Member> =
        usersRepository.loadMyProfile()
}
