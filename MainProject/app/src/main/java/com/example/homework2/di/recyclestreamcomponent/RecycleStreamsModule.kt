package com.example.homework2.di.recyclestreamcomponent

import com.example.homework2.data.ZulipDataBase
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamModel
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamPresenter
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamPresenterImpl
import com.example.homework2.mvp.streams.recyclestream.RecycleStreamsModelImpl
import com.example.homework2.repositories.StreamRepository
import com.example.homework2.repositories.StreamsRepositoryImpl
import com.example.homework2.retrofit.RetrofitService
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

    @Provides
    fun providesStreamsRepository(
        retrofitService: RetrofitService,
        dataBase: ZulipDataBase
    ): StreamRepository {
        return StreamsRepositoryImpl(retrofitService = retrofitService, database = dataBase)
    }
}
