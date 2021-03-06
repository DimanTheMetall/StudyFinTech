package com.example.homework2.di.chatcomponent

import com.example.homework2.data.ZulipDataBase
import com.example.homework2.mvp.chat.ChatModel
import com.example.homework2.mvp.chat.ChatModelImpl
import com.example.homework2.mvp.chat.ChatPresenter
import com.example.homework2.mvp.chat.ChatPresenterImpl
import com.example.homework2.repositories.ChatRepository
import com.example.homework2.repositories.ChatRepositoryImpl
import com.example.homework2.repositories.StreamRepository
import com.example.homework2.repositories.StreamsRepositoryImpl
import com.example.homework2.retrofit.RetrofitService
import dagger.Module
import dagger.Provides

@Module
class ChatModule {

    @Provides
    fun provideModelImpl(modelImpl: ChatModelImpl): ChatModel {
        return modelImpl
    }

    @Provides
    fun providePresenterImpl(model: ChatModel): ChatPresenter {
        return ChatPresenterImpl(model)
    }

    @Provides
    fun providesChatRepository(
        retrofitService: RetrofitService,
        dataBase: ZulipDataBase
    ): ChatRepository {
        return ChatRepositoryImpl(retrofitService = retrofitService, database = dataBase)
    }

    @Provides
    fun providesStreamsRepository(
        retrofitService: RetrofitService,
        dataBase: ZulipDataBase
    ): StreamRepository {
        return StreamsRepositoryImpl(retrofitService = retrofitService, database = dataBase)
    }

}
