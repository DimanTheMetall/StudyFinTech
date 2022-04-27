package com.example.homework2.mvp.myprofile

import com.example.homework2.mvp.BasePresenterImpl
import javax.inject.Inject

class MyProfilePresenterImpl @Inject constructor(
    model: MyProfileModel
) : BasePresenterImpl<MyProfileView, MyProfileModel>(model), MyProfilePresenter {

    override fun onInit() {
        val disposable = model.getMyProfile()
            .subscribe({ view.renderProfile(it) }, {})
        compositeDisposable.add(disposable)
    }
}
