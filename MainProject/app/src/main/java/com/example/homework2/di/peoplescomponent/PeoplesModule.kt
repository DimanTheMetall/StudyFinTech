package com.example.homework2.di.peoplescomponent

import com.example.homework2.mvp.peoples.PeoplesModel
import com.example.homework2.mvp.peoples.PeoplesModelImpl
import com.example.homework2.mvp.peoples.PeoplesPresenter
import com.example.homework2.mvp.peoples.PeoplesPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class PeoplesModule {

    @Provides
    fun provideModelImpl(peoplesModelImpl: PeoplesModelImpl): PeoplesModel {
        return peoplesModelImpl
    }


    @Provides
    fun providesPresenterImpl(model: PeoplesModel): PeoplesPresenter {
        return PeoplesPresenterImpl(model)
    }
}
