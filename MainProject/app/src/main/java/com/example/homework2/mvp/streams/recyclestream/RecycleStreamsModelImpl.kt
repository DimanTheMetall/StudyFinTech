package com.example.homework2.mvp.streams.recyclestream

import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity
import com.example.homework2.dataclasses.chatdataclasses.ResultResponse
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Subscriptions
import com.example.homework2.mvp.BaseModelImpl
import com.example.homework2.repositories.StreamRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class RecycleStreamsModelImpl @Inject constructor(
    private val streamsRepositoryImpl: StreamRepository
) : BaseModelImpl(), RecycleStreamModel {


    override fun insertStreamsAndTopics(
        streamsList: List<Stream>,
        isSubscribed: Boolean
    ): Completable {
        return streamsRepositoryImpl.insertStreamsAndTopics(
            streamsList = streamsList,
            isSubscribed = isSubscribed
        )
    }

    override fun loadSubscribedStreams(): Single<List<Stream>> {
        return streamsRepositoryImpl.loadSubscribedStreams()
    }

    override fun loadAllStreams(): Single<List<Stream>> {
        return streamsRepositoryImpl.loadAllStreams()
    }

    override fun selectAllStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>> =
        streamsRepositoryImpl.selectAllStreamsAndTopics()

    override fun selectSubscribedStreamsAndTopics(): Single<Map<StreamEntity, List<TopicEntity>>> =
        streamsRepositoryImpl.selectSubscribedStreamsAndTopics()

    override fun createOrSubscribeStream(subscriptions: Subscriptions): Single<ResultResponse> {
        return streamsRepositoryImpl.createOrSubscribeStream(subscriptions = subscriptions)
    }

}
