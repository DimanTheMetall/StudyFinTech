package com.example.homework2.mvp.streams.recyclestream

import com.example.homework2.dataclasses.ResultStream
import com.example.homework2.dataclasses.Stream
import com.example.homework2.mvp.BasePresenterImpl
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class RecycleStreamPresenterImpl(
    view: RecycleStreamView,
    model: RecycleStreamModel
) : BasePresenterImpl<RecycleStreamView, RecycleStreamModel>(view, model), RecycleStreamPresenter {

    private val allChannelList = mutableListOf<Stream>()
    private var subscribedList = mutableListOf<Stream>()

    val allChannelsObservable: Observable<ResultStream>
        get() = allChannelsSubject
    private val allChannelsSubject =
        BehaviorSubject.create<ResultStream>().apply {
            onNext(ResultStream.Success(allChannelList)) //Затычка
        }

    val subscribedChannelsObservable: Observable<ResultStream>
        get() = subscribedChannelsSubject
    private val subscribedChannelsSubject = BehaviorSubject.create<ResultStream>().apply {
        onNext(ResultStream.Success(subscribedList)) //Затычка
    }


    override fun searchedTextChangedAllStreams(text: String) {
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

    override fun searchedTextChangedSubscribedStreams(text: String) {
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