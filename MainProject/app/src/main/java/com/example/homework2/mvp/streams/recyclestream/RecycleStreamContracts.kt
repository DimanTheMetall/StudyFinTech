package com.example.homework2.mvp.streams.recyclestream

import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.Stream
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Flowable
import io.reactivex.Single

interface RecycleStreamView : BaseView {

    fun initRecycleAdapter()

    fun initShimmer()

    fun initSearchTextListener()

    fun loadStreamsFromZulip()

    fun showProgress()

    fun showError()

    fun showStreams(streamList: List<Stream>)


}

interface RecycleStreamPresenter : BasePresenter {

    fun searchedTextChangedAllStreams(text: String)

    fun searchedTextChangedSubscribedStreams(text: String)

    fun onAllStreamsNeeded()

    fun onSubscribedStreamsNeeded()

}

interface RecycleStreamModel : BaseModel {

    fun insertStreamsANdTopics(streamsList: List<Stream>, isSubscribed: Boolean)

    fun loadSubscribedStreams(): Single<List<Stream>>

    fun loadAllStreams(): Single<List<Stream>>

    fun selectAllStreamsAndTopics(): Flowable<Map<StreamEntity, List<TopicEntity>>>

    fun selectSubscribedStreamsAndTopics(): Flowable<Map<StreamEntity, List<TopicEntity>>>
}