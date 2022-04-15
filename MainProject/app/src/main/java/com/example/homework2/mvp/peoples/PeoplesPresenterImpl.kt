package com.example.homework2.mvp.peoples

import com.example.homework2.mvp.BasePresenterImpl

class PeoplesPresenterImpl(
    view: PeoplesView,
    model: PeoplesModel
) : BasePresenterImpl<PeoplesView, PeoplesModel>(view, model), PeoplesPresenter {

    override fun onSearchedTextChanged(searchedText: String) {
        val disposable = model.loadAllUsersWithOutPresence()
            .subscribe({ members ->
                view.showUsers(members.filter { it.full_name.contains(other = searchedText) })
            }, { view.showError() })

        compositeDisposable.add(disposable)
    }

    override fun onInit() {
        view.initShimmer()
        view.configureRecycleAdapter()
        view.showProgress()
        view.initSearchedTextListener()
        val disposable = model.loadAllUsersWithPresence()
            .subscribe({ view.showUsers(it) }, { view.showError() })

        compositeDisposable.add(disposable)
    }

}