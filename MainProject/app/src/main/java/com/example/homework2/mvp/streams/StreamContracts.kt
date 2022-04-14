package com.example.homework2.mvp.streams

import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView

interface StreamsView : BaseView {

    fun initViewPager()

}

interface StreamsPresenter : BasePresenter {

}

interface StreamsModel : BaseModel {
}
