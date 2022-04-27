package com.example.homework2.mvp.peoples

import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.repositories.UsersRepository
import io.reactivex.Single
import javax.inject.Inject

class PeoplesModelImpl @Inject constructor(
    private val usersRepository: UsersRepository
) : BaseModelImpl(), PeoplesModel {

    override fun getUsersWithOutPresence(): Single<List<Member>> {
        return usersRepository.loadAllUsersWithOutPresence()

    }

    override fun getAllUsersWithPresence(): Single<List<Member>> {
        return usersRepository.loadAllUsersWithPresence()

    }

}