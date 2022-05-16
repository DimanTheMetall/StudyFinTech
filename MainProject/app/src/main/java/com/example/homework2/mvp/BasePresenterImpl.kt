package com.example.homework2.mvp

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenterImpl<V : BaseView, M : BaseModel>(
    protected val model: M
) : BasePresenter {

    private var localView: V? = null
    protected val view: V
        get() = localView!!
    protected val compositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        compositeDisposable.clear()
        localView = null
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAttach(baseView: BaseView) {
        localView = baseView as V
    }
}
