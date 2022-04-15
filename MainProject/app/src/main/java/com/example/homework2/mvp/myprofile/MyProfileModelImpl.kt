package com.example.homework2.mvp.myprofile

import com.example.homework2.dataclasses.Member
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MyProfileModelImpl(
    private val retrofitService: RetrofitService
) : BaseModelImpl(), MyProfileModel {

    override fun loadMyProfile(): Single<Member> =
        retrofitService.getOwnUser()
            .subscribeOn(Schedulers.io())
            .flatMap { member ->
                retrofitService.getPresence(user_id_or_email = member.email)
                    .flatMap { Single.just(member.copy(website = it.presence.website)) }
            }
            .observeOn(AndroidSchedulers.mainThread())
}