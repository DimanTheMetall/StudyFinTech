package com.example.homework2.mvp

interface BaseView {

    fun configureActionBar()

}

interface BasePresenter {

    fun onDestroyView()

    fun onAttach(baseView: BaseView)
}

interface BaseModel



