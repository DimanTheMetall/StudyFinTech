package com.example.homework2.mvp.streams.recyclestream

import android.util.Log
import com.example.homework2.Constance
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.repositories.StreamsRepository
import io.reactivex.Single
import javax.inject.Inject

class RecycleStreamsModelImpl @Inject constructor(
    private val streamsRepository: StreamsRepository
) : BaseModelImpl(), RecycleStreamModel {


    override fun insertStreamsAndTopics(streamsList: List<Stream>, isSubscribed: Boolean) {
        val disposable =
            streamsRepository.insertStreamsAndTopics(
                streamsList = streamsList,
                isSubscribed = isSubscribed
            )
                .subscribe(
                    { Log.d(Constance.LogTag.TOPIC_AND_STREAM, "INSERT SUCCESS") },
                    { Log.e(Constance.LogTag.TOPIC_AND_STREAM, it.toString()) })

        compositeDisposable.add(disposable)
    }

    override fun loadSubscribedStreams(): Single<List<Stream>> {
        return streamsRepository.loadSubscribedStreams()
    }

    override fun loadAllStreams(): Single<List<Stream>> {
        return streamsRepository.loadAllStreams()
    }

    override fun selectAllStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>> =
        streamsRepository.selectAllStreamsAndTopics()


    override fun selectSubscribedStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>> =
        streamsRepository.selectSubscribedStreamsAndTopics()

}

