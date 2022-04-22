package com.example.homework2.di

import com.example.homework2.mvp.streams.recyclestream.RecycleStreamsFragment
import dagger.Component
import javax.inject.Scope


@RecycleStreamsScope
@Component(
    modules = [RecycleStreamsModule::class],
    dependencies = [ZulipComponent::class]
)
interface RecycleStreamsComponent {

    fun inject(recycleStreamFragment: RecycleStreamsFragment)

    @Component.Factory
    interface Factory {

        fun create(zulipComponent: ZulipComponent): RecycleStreamsComponent
    }

}

@Scope
annotation class RecycleStreamsScope
