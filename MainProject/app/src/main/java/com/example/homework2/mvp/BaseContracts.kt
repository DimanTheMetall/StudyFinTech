package com.example.homework2.mvp

interface BaseView {

    fun configureActionBar()

}

interface BasePresenter {

    fun onDestroyView()

    fun onInit()

    fun onAttach(baseView: BaseView)
}

interface BaseModel {

    fun clearDisposable()
}
