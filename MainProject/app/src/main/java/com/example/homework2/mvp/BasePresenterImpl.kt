package com.example.homework2.mvp

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenterImpl<V : BaseView, M : BaseModel>(
    protected val view: V,
    protected val model: M
) : BasePresenter {

    protected val compositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        model.clearDisposable()
        compositeDisposable.clear()
    }
}