package com.example.homework2.mvp.peoples

import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.mvp.BasePresenterImpl
import com.example.homework2.toErrorType
import javax.inject.Inject

class PeoplesPresenterImpl @Inject constructor(
    model: PeoplesModel
) : BasePresenterImpl<PeoplesView, PeoplesModel>(model), PeoplesPresenter {

    override fun onProfileCLicked(member: Member) {
        view.openProfileFragment(member = member)
    }

    override fun onSearchedTextChanged(searchedText: String) {
        view.showProgress()
        val disposable = model.getUsersWithOutPresence()
            .subscribe({ members ->
                view.showUsers(members.filter { it.fullName.contains(other = searchedText) })
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(disposable)
    }

    override fun onStart() {
        view.showProgress()
        val disposable = model.getAllUsersWithPresence()
            .subscribe({
                view.showUsers(userList = it)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(disposable)
    }

}
