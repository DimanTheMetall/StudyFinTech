package com.example.homework2.mvp.myprofile

import com.example.homework2.mvp.BasePresenterImpl

class MyProfilePresenterImpl(
    model: MyProfileModel
) : BasePresenterImpl<MyProfileView, MyProfileModel>(model), MyProfilePresenter {

    override fun onInit() {
        val disposable = model.loadMyProfile()
            .subscribe({ view.renderProfile(it) }, {})
        compositeDisposable.add(disposable)
    }
}
