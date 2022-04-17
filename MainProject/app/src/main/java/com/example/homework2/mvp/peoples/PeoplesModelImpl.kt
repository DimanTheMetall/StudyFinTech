package com.example.homework2.mvp.peoples

import com.example.homework2.Constance
import com.example.homework2.dataclasses.chatdataclasses.Website
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PeoplesModelImpl(
    private val retrofitService: RetrofitService
) : BaseModelImpl(), PeoplesModel {

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
                retrofitService.getPresence(member.user_id)
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

}