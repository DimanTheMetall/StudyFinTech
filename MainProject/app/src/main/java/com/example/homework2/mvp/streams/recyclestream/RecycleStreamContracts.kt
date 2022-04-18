package com.example.homework2.mvp.streams.recyclestream

import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Single

interface RecycleStreamView : BaseView {

    fun showProgress()

    fun showError()

    fun showStreams(streamList: List<Stream>)

}

interface RecycleStreamPresenter : BasePresenter {

    fun onSearchedTextChangedAllStreams(text: String)

    fun onSearchedTextChangedSubscribedStreams(text: String)

    fun onAllStreamsNeeded()

    fun onSubscribedStreamsNeeded()

}

interface RecycleStreamModel : BaseModel {

    fun insertStreamsAndTopics(streamsList: List<Stream>, isSubscribed: Boolean)

    fun loadSubscribedStreams(): Single<List<Stream>>

    fun loadAllStreams(): Single<List<Stream>>

    fun selectAllStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>>

    fun selectSubscribedStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>>
}
