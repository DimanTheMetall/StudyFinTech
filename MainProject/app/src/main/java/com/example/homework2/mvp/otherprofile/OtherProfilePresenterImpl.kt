package com.example.homework2.mvp.otherprofile

import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BasePresenterImpl

class OtherProfilePresenterImpl(
    model: OtherProfileModel
) : BasePresenterImpl<OtherProfileView, OtherProfileModel>(model), OtherProfilePresenter {

    override fun onUserNeededUpdate(member: Member) {
        val disposable = model.loadPresence(member = member)
            .subscribe({ view.setStatus(member = member, presence = it) }, {})

        compositeDisposable.add(disposable)
    }

    override fun onInit() {

    }
}