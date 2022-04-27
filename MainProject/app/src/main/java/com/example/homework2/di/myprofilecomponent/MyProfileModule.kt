package com.example.homework2.di.myprofilecomponent

import com.example.homework2.mvp.myprofile.MyProfileModel
import com.example.homework2.mvp.myprofile.MyProfileModelImpl
import com.example.homework2.mvp.myprofile.MyProfilePresenter
import com.example.homework2.mvp.myprofile.MyProfilePresenterImpl
import com.example.homework2.repositories.UsersRepository
import com.example.homework2.retrofit.RetrofitService
import dagger.Module
import dagger.Provides

@Module
class MyProfileModule {

    @Provides
    fun providesModelImpl(modelImpl: MyProfileModelImpl): MyProfileModel {
        return modelImpl
    }

    @Provides
    fun providePresenterImpl(model: MyProfileModel): MyProfilePresenter {
        return MyProfilePresenterImpl(model = model)
    }

    @Provides
    fun provideUsersRepository(retrofitService: RetrofitService): UsersRepository {
        return UsersRepository(retrofitService = retrofitService)
    }
}
