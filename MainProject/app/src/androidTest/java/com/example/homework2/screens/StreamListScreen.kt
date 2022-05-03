package com.example.homework2.screens

import android.view.View
import com.example.homework2.R
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamsFragment
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import org.hamcrest.Matcher

object StreamListScreen : KScreen<StreamListScreen>() {

    override val layoutId: Int
        get() = R.layout.fragment_recycle_channels

    override val viewClass: Class<*>
        get() = RecycleStreamsFragment::class.java

    val streamList =
        KRecyclerView({ withId(R.id.recycle_channel) }, { itemType { StreamItem(it) } })

    class StreamItem(parent: Matcher<View>) : KRecyclerItem<StreamItem>(parent) {
        val topicList = KView(parent) { withId(R.id.topic_list) }
    }
}
