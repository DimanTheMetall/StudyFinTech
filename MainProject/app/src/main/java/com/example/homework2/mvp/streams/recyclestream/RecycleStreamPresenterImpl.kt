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
        val disposable = model.getAllStreams()
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
        val disposable = model.getSubscribedStreams()
            .subscribe({ streams ->
                val searchedStreams: List<Stream> = streams.filter { it.name.contains(text) }
                view.showStreams(streamList = searchedStreams)
            }, {
                view.showError()
            })
        compositeDisposable.add(disposable)
    }

    override fun onInit() {
        view.initRecycleAdapter()
        view.initShimmer()
        view.initSearchTextListener()
    }


}