package com.example.homework2.mvp.otherprofile

import com.example.homework2.dataclasses.chatdataclasses.Presence
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.retrofit.RetrofitService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OtherProfileModelImpl(
    private val retrofitService: RetrofitService
) : BaseModelImpl(), OtherProfileModel {

    override fun loadPresence(member: Member): Single<Presence> {
        return retrofitService.getPresence(member.email)
            .subscribeOn(Schedulers.io())
            .map { it.presence }
            .observeOn(AndroidSchedulers.mainThread())
    }

}