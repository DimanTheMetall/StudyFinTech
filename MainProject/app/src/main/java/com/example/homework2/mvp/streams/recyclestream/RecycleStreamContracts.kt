package com.example.homework2.mvp.streams.recyclestream

import androidx.fragment.app.Fragment
import com.example.homework2.Errors
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.chatdataclasses.JsonResponse
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Subscriptions
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BaseModel
import com.example.homework2.mvp.BasePresenter
import com.example.homework2.mvp.BaseView
import io.reactivex.Single

interface RecycleStreamView : BaseView {

    fun showProgress()

    fun showError(throwable: Throwable, error: Errors)

    fun showStreams(streamList: List<Stream>)

    fun showBottomSheetDialog()

    fun hideBottomSheetDialog()

    fun showProgressInDialog()

    fun stopShowingProgressInDialog()

    fun getIsSubscribeBoolean(): Boolean

    fun openFragment(fragment: Fragment, tag: String? = null)

}

interface RecycleStreamPresenter : BasePresenter {

    fun onSearchedTextChangedAllStreams(text: String)

    fun onSearchedTextChangedSubscribedStreams(text: String)

    fun onAllStreamsNeeded()

    fun onSubscribedStreamsNeeded()

    fun onLastStreamClick()

    fun onCreateButtonCLick(streamName: String, streamDescription: String?)

    fun onCancelButtonClick()

    fun onLongTapOnStream(stream: Stream)

    fun onShortClickOnTopic(topic: Topic, stream: Stream)

}

interface RecycleStreamModel : BaseModel {

    fun insertStreamsAndTopics(streamsList: List<Stream>, isSubscribed: Boolean)

    fun loadSubscribedStreams(): Single<List<Stream>>

    fun loadAllStreams(): Single<List<Stream>>

    fun selectAllStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>>

    fun selectSubscribedStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>>

    fun createOrSubscribeStream(subscriptions: Subscriptions): Single<JsonResponse>
}
