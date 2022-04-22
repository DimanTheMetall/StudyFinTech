package com.example.homework2.mvp.streams

import com.example.homework2.mvp.BasePresenterImpl

class StreamPresenterImpl(
    model: StreamsModel
) : BasePresenterImpl<StreamsView, StreamsModel>(model), StreamsPresenter {

    override fun onInit() {
    }
}
