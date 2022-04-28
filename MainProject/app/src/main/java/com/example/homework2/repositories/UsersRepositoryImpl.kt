package com.example.homework2.repositories

import com.example.homework2.Constance
import com.example.homework2.dataclasses.chatdataclasses.Presence
import com.example.homework2.dataclasses.chatdataclasses.Website
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface UsersRepository {

    fun loadAllUsersWithOutPresence(): Single<List<Member>>

    fun loadAllUsersWithPresence(): Single<List<Member>>

    fun loadMyProfile(): Single<Member>

    fun loadPresence(member: Member): Single<Presence>
}

class UsersRepositoryImpl @Inject constructor(val retrofitService: RetrofitService) :
    UsersRepository {

    override fun loadAllUsersWithOutPresence(): Single<List<Member>> {
        return retrofitService.getUsers()
            .subscribeOn(Schedulers.io())
            .map { it.members }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadAllUsersWithPresence(): Single<List<Member>> {
        return retrofitService.getUsers()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { Observable.fromIterable(it.members) }
            .flatMap { member ->
                retrofitService.getPresence(member.userId)
                    .map { it.presence.website }
                    .onErrorReturnItem(
                        Website(Constance.Status.OFFLINE, -1)
                    )
                    .map { website ->
                        member.copy(website = website)
                    }.toObservable()
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())

    }

    override fun loadMyProfile(): Single<Member> =
        retrofitService.getOwnUser()
            .subscribeOn(Schedulers.io())
            .flatMap { member ->
                retrofitService.getPresence(user_id_or_email = member.email)
                    .flatMap { Single.just(member.copy(website = it.presence.website)) }
            }
            .observeOn(AndroidSchedulers.mainThread())

    override fun loadPresence(member: Member): Single<Presence> {
        return retrofitService.getPresence(member.email)
            .subscribeOn(Schedulers.io())
            .map { it.presence }
            .observeOn(AndroidSchedulers.mainThread())
    }
}
