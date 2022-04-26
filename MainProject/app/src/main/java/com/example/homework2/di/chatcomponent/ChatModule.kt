package com.example.homework2.di.chatcomponent

import com.example.homework2.mvp.chat.ChatModel
import com.example.homework2.mvp.chat.ChatModelImpl
import com.example.homework2.mvp.chat.ChatPresenter
import com.example.homework2.mvp.chat.ChatPresenterImpl
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
}
