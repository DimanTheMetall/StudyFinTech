package com.example.homework2.mvp.streams.recyclestream

import com.example.homework2.Errors
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Subscriptions
import com.example.homework2.mvp.BasePresenterImpl
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
                view.showError(it, Errors.INTERNET)
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
                view.showError(it, Errors.INTERNET)
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
            Subscriptions(
                name = streamName,
                description = streamDescription
            )
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
                view.showError(it, Errors.INTERNET)
            })

        compositeDisposable.add(disposable)
    }

    override fun onCancelButtonClick() {
        view.hideBottomSheetDialog()
        view.stopShowingProgressInDialog()
    }

    override fun onInit() {}

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
                    .subscribe({
                        view.showStreams(it)
                        model.insertStreamsAndTopics(streamsList = it, isSubscribed = false)
                    }, { view.showError(it, Errors.INTERNET) })

                compositeDisposable.add(disposable)
            }
            false -> {
                val disposable = model.loadSubscribedStreams()
                    .subscribe({
                        view.showStreams(it)
                        model.insertStreamsAndTopics(streamsList = it, isSubscribed = true)
                    }, { view.showError(it, Errors.INTERNET) })

                compositeDisposable.add(disposable)
            }
        }
    }

}
