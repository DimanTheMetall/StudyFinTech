package com.example.homework2.mvp.streams.recyclestream

import com.example.homework2.dataclasses.Stream
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Single

interface RecycleStreamView : BaseView {

    fun initRecycleAdapter()

    fun initShimmer()

    fun initSearchTextListener()

    fun showProgress()

    fun showError()

    fun showStreams(streamList: List<Stream>)

}

interface RecycleStreamPresenter : BasePresenter {

    fun searchedTextChangedAllStreams(text: String)

    fun searchedTextChangedSubscribedStreams(text: String)

}

interface RecycleStreamModel : BaseModel {

    fun getSubscribedStreams(): Single<List<Stream>>

    fun getAllStreams(): Single<List<Stream>>
}