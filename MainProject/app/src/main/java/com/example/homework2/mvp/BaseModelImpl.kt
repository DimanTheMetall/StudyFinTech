package com.example.homework2.mvp

import io.reactivex.disposables.CompositeDisposable

abstract class BaseModelImpl : BaseModel {

    val compositeDisposable = CompositeDisposable()

    override fun clearDisposable() {
        compositeDisposable.clear()
    }
}
