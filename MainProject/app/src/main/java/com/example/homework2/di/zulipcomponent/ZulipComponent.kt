package com.example.homework2.di.zulipcomponent


import android.content.Context
import com.example.homework2.data.ZulipDataBase
import com.example.homework2.retrofit.RetrofitService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, RoomModule::class])
interface ZulipComponent {

    fun provideRetrofitService(): RetrofitService

    fun provideZulipDataBase(): ZulipDataBase

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context: Context): ZulipComponent
    }
}
