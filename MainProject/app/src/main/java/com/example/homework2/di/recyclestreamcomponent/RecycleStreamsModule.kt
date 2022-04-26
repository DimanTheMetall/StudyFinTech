package com.example.homework2.di.recyclestreamcomponent

import com.example.homework2.mvp.streams.recyclestream.RecycleStreamModel
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamPresenter
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamPresenterImpl
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamsModelImpl
import dagger.Module
import dagger.Provides

@Module
class RecycleStreamsModule {

    @Provides
    fun providesModelImpl(
        recycleStreamModel: RecycleStreamsModelImpl
    ): RecycleStreamModel {
        return recycleStreamModel
    }

    @Provides
    fun providesPresenterImpl(model: RecycleStreamModel): RecycleStreamPresenter {
        return RecycleStreamPresenterImpl(model = model)
    }
}
