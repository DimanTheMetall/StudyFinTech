package com.example.homework2.mvp.peoples

import com.example.homework2.mvp.BasePresenterImpl
import javax.inject.Inject

class PeoplesPresenterImpl @Inject constructor(
    model: PeoplesModel
) : BasePresenterImpl<PeoplesView, PeoplesModel>(model), PeoplesPresenter {

    override fun onSearchedTextChanged(searchedText: String) {
        view.showProgress()
        val disposable = model.getUsersWithOutPresence()
            .subscribe({ members ->
                view.showUsers(members.filter { it.fullName.contains(other = searchedText) })
            }, { view.showError(it) })

        compositeDisposable.add(disposable)
    }

    override fun onInit() {
        view.showProgress()
        val disposable = model.getAllUsersWithPresence()
            .subscribe({ view.showUsers(it) }, { view.showError(it) })

        compositeDisposable.add(disposable)
    }

}
