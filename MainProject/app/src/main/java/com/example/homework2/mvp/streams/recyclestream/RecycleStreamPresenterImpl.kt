package com.example.homework2.mvp.streams.recyclestream

import android.util.Log
import com.example.homework2.Constants
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Subscriptions
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.BasePresenterImpl
import com.example.homework2.mvp.chat.ChatFragment
import com.example.homework2.toErrorType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecycleStreamPresenterImpl(
    model: RecycleStreamModel
) : BasePresenterImpl<RecycleStreamView, RecycleStreamModel>(model), RecycleStreamPresenter {

    override fun onSearchedTextChangedAllStreams(text: String) {
        view.showProgress()
        val disposable = model.loadAllStreams()
            .subscribe({ streams ->
                val searchedStreams: List<Stream> = streams.filter { it.name.contains(text) }
                view.showStreams(streamList = searchedStreams)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(disposable)
    }

    override fun onSearchedTextChangedSubscribedStreams(text: String) {
        view.showProgress()
        val disposable = model.loadSubscribedStreams()
            .subscribe({ streams ->
                val searchedStreams: List<Stream> = streams.filter { it.name.contains(text) }
                view.showStreams(streamList = searchedStreams)
            }, {
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(disposable)
    }

    override fun onAllStreamsNeeded() {
        val selectDisposable = model.selectAllStreamsAndTopics()
            .subscribe { map ->
                view.showStreams(streamList = mapStreams(map = map))
                loadStreams(true)
            }

        compositeDisposable.add(selectDisposable)
    }

    override fun onSubscribedStreamsNeeded() {
        val selectDisposable = model.selectSubscribedStreamsAndTopics()
            .subscribe { map ->
                view.showStreams(streamList = mapStreams(map = map))
                loadStreams(false)
            }

        compositeDisposable.add(selectDisposable)
    }

    override fun onLastStreamClick() {
        view.showBottomSheetDialog()
    }

    override fun onCreateButtonCLick(streamName: String, streamDescription: String?) {
        view.showProgressInDialog()
        val disposable = model.createOrSubscribeStream(
            Subscriptions(name = streamName, description = streamDescription)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.stopShowingProgressInDialog()
                view.hideBottomSheetDialog()
                if (view.getIsSubscribeBoolean()) {
                    onSubscribedStreamsNeeded()
                } else {
                    onAllStreamsNeeded()
                }
            }, {
                view.stopShowingProgressInDialog()
                view.showError(throwable = it, error = it.toErrorType())
            })

        compositeDisposable.add(disposable)
    }

    override fun onCancelButtonClick() {
        view.hideBottomSheetDialog()
        view.stopShowingProgressInDialog()
    }

    override fun onLongTapOnStream(stream: Stream) {
        view.openFragment(
            fragment = ChatFragment.newInstance(
                stream = stream,
                topic = Topic(name = Constants.NOT_EXIST_TOPIC),
            ),
            tag = ChatFragment.TAG
        )
    }

    override fun onShortClickOnTopic(topic: Topic, stream: Stream) {
        view.openFragment(
            fragment = ChatFragment.newInstance(topic = topic, stream = stream),
            tag = ChatFragment.TAG
        )
    }

    private fun mapStreams(map: Map<StreamEntity, List<TopicEntity>>): List<Stream> {
        val streamList = mutableListOf<Stream>()
        map.keys.forEach { streamEntity ->
            val stream = streamEntity.toStream()
            val topicList = map.getValue(streamEntity).map { it.toTopic() }
            stream.topicList = topicList.toMutableList()
            streamList.add(stream)
        }
        return streamList
    }

    private fun loadStreams(isAllNeeded: Boolean) {
        view.showProgress()
        when (isAllNeeded) {
            true -> {
                val disposable = model.loadAllStreams()
                    .subscribe({ allStreamList ->
                        view.showStreams(streamList = allStreamList)
                        val insertDisposable =
                            model.insertStreamsAndTopics(
                                streamsList = allStreamList,
                                isSubscribed = false
                            )
                                .subscribe(
                                    { },
                                    { Log.e(Constants.LogTag.TOPIC_AND_STREAM, it.toString()) })
                        compositeDisposable.add(insertDisposable)
                    }, {
                        view.showError(throwable = it, error = it.toErrorType())
                    })

                compositeDisposable.add(disposable)
            }
            false -> {
                val disposable = model.loadSubscribedStreams()
                    .subscribe({ subscribedStreamList ->
                        view.showStreams(streamList = subscribedStreamList)
                        val insertDisposable =
                            model.insertStreamsAndTopics(
                                streamsList = subscribedStreamList,
                                isSubscribed = true
                            )
                                .subscribe(
                                    { },
                                    { Log.e(Constants.LogTag.TOPIC_AND_STREAM, it.toString()) })
                        compositeDisposable.add(insertDisposable)
                    }, {
                        view.showError(throwable = it, error = it.toErrorType())
                    })

                compositeDisposable.add(disposable)
            }
        }
    }

}
