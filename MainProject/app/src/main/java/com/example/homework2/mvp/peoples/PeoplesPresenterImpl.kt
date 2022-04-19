package com.example.homework2.mvp.peoples

import com.example.homework2.mvp.BasePresenterImpl

class PeoplesPresenterImpl(
    view: PeoplesView,
    model: PeoplesModel
) : BasePresenterImpl<PeoplesView, PeoplesModel>(view, model), PeoplesPresenter {

    override fun onSearchedTextChanged(searchedText: String) {
        view.showProgress()
        val disposable = model.loadAllUsersWithOutPresence()
            .subscribe({ members ->
                view.showUsers(members.filter { it.fullName.contains(other = searchedText) })
            }, { view.showError(it) })

        compositeDisposable.add(disposable)
    }

    override fun onInit() {
        view.showProgress()
        val disposable = model.loadAllUsersWithPresence()
            .subscribe({ view.showUsers(it) }, { view.showError(it) })

        compositeDisposable.add(disposable)
    }

}
