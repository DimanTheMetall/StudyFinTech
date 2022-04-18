package com.example.homework2.mvp.streams

import com.example.homework2.mvp.BasePresenterImpl

class StreamPresenterImpl(
    view: StreamsView,
    model: StreamsModel
) : BasePresenterImpl<StreamsView, StreamsModel>(view, model), StreamsPresenter {


    override fun onInit() {

    }

}