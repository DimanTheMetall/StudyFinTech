package com.example.homework2.test.mvp.streams.recyclestream

import com.example.homework2.RxRule
import com.example.homework2.dataclasses.streamsandtopics.Stream
import com.example.homework2.dataclasses.streamsandtopics.Topic
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamModel
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamPresenterImpl
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamView
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class RecycleStreamPresenterImplTest {

    @get:Rule
    val rxRule = RxRule()

    private val model: RecycleStreamModel = Mockito.mock(RecycleStreamModel::class.java)
    private val presenterImplTest = RecycleStreamPresenterImpl(model = model)
    private val view: RecycleStreamView = Mockito.mock(RecycleStreamView::class.java)

    @Before
    fun setUp() {
        presenterImplTest.onAttach(view)
    }

    @Test
    fun `should streams searched`() {
        val topicList = createTopicList(name = "topic")
        val streamList = createStreamList(streamId = 20, name = "streamName", topicList = topicList)
        Mockito.`when`(model.loadAllStreams()).thenReturn(Single.just(streamList))

        presenterImplTest.onSearchedTextChangedAllStreams("searched text")

        Mockito.verify(view, Mockito.times(1)).showProgress()
        Mockito.verify(view, Mockito.times(1)).showStreams(Mockito.anyList())
    }

    private fun createStreamList(
        streamId: Int,
        name: String,
        topicList: MutableList<Topic>
    ): List<Stream> {
        val stream = Stream(streamId, name, topicList)
        return listOf(stream, stream, stream)
    }

    private fun createTopicList(name: String): MutableList<Topic> {
        val topic = Topic(name)
        return mutableListOf(topic, topic, topic)
    }

}
