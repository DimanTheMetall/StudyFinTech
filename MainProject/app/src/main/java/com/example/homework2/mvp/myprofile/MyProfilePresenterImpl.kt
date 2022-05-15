package com.example.homework2.mvp.myprofile

import com.example.homework2.mvp.BasePresenterImpl
import com.example.homework2.toErrorType
import javax.inject.Inject

class MyProfilePresenterImpl @Inject constructor(
    model: MyProfileModel
) : BasePresenterImpl<MyProfileView, MyProfileModel>(model), MyProfilePresenter {

    override fun onInit() {
        val disposable = model.getMyProfile()
            .subscribe({ view.renderProfile(member = it) }, {
                view.showError(
                    throwable = it,
                    error = it.toErrorType()
                )
            })
        compositeDisposable.add(disposable)
    }
}
