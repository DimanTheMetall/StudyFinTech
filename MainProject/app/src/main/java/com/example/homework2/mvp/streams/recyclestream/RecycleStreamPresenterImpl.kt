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
                view.showError()
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
                view.showError()
            })
        compositeDisposable.add(disposable)
    }

    override fun onAllStreamsNeeded() {
        view.showProgress()
        val selectDisposable = model.selectAllStreamsAndTopics()
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

        view.showProgress()

        val disposable = model.loadAllStreams()
            .subscribe({
                view.showStreams(it)
                model.insertStreamsANdTopics(streamsList = it, isSubscribed = false)
            }, { view.showError() })

        compositeDisposable.add(disposable)
        compositeDisposable.add(selectDisposable)
    }

    override fun onSubscribedStreamsNeeded() {
        view.showProgress()
        val selectDisposable = model.selectSubscribedStreamsAndTopics()
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

        view.showProgress()

        val disposable = model.loadSubscribedStreams()
            .subscribe({
                view.showStreams(it)
                model.insertStreamsANdTopics(streamsList = it, isSubscribed = true)
            }, { view.showError() })

        compositeDisposable.add(selectDisposable)
        compositeDisposable.add(disposable)
    }

    override fun onInit() {
        view.initRecycleAdapter()
        view.initShimmer()
        view.initSearchTextListener()
        view.loadStreamsFromZulip()
    }

}