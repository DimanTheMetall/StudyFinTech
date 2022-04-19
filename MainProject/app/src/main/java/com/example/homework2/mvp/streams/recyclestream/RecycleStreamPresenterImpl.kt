package com.example.homework2.mvp.streams.recyclestream

import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.mvp.BasePresenterImpl

class RecycleStreamPresenterImpl(
    view: RecycleStreamView,
    model: RecycleStreamModel
) : BasePresenterImpl<RecycleStreamView, RecycleStreamModel>(view, model), RecycleStreamPresenter {

    override fun onSearchedTextChangedAllStreams(text: String) {
        view.showProgress()
        val disposable = model.loadAllStreams()
            .subscribe({ streams ->
                val searchedStreams: List<Stream> = streams.filter { it.name.contains(text) }
                view.showStreams(streamList = searchedStreams)
            }, {
                view.showError(it)
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
                view.showError(it)
            })
        compositeDisposable.add(disposable)
    }

    override fun onAllStreamsNeeded() {
        val selectDisposable = model.selectAllStreamsAndTopics()
            .doOnSubscribe { view.showProgress() }
            .subscribe { map ->
                val streamList = mutableListOf<Stream>()
                map.keys.forEach { streamEntity ->
                    val stream = streamEntity.toStream()
                    val topicList = map.getValue(streamEntity).map { it.toTopic() }
                    stream.topicList = topicList.toMutableList()
                    streamList.add(stream)
                }
                view.showStreams(streamList = streamList)
            }

        val disposable = model.loadAllStreams()
            .doOnSubscribe { view.showProgress() }
            .subscribe({
                view.showStreams(it)
                model.insertStreamsAndTopics(streamsList = it, isSubscribed = false)
            }, { view.showError(it) })

        compositeDisposable.add(disposable)
        compositeDisposable.add(selectDisposable)
    }

    override fun onSubscribedStreamsNeeded() {
        val selectDisposable = model.selectSubscribedStreamsAndTopics()
            .doOnSubscribe { view.showProgress() }
            .subscribe { map ->
                val streamList = mutableListOf<Stream>()
                map.keys.forEach { streamEntity ->
                    val stream = streamEntity.toStream()
                    val topicList = map.getValue(streamEntity).map { it.toTopic() }
                    stream.topicList = topicList.toMutableList()
                    streamList.add(stream)
                }
                view.showStreams(streamList = streamList)
            }

        val disposable = model.loadSubscribedStreams()
            .doOnSubscribe { view.showProgress() }
            .subscribe({
                view.showStreams(it)
                model.insertStreamsAndTopics(streamsList = it, isSubscribed = true)
            }, { view.showError(it) })

        compositeDisposable.add(selectDisposable)
        compositeDisposable.add(disposable)
    }

    override fun onInit() {
    }

}