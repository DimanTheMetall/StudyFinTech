package com.example.homework2.di.otherprofilecomponent

import com.example.homework2.mvp.otherprofile.OtherProfileModel
import com.example.homework2.mvp.otherprofile.OtherProfileModelImpl
import com.example.homework2.mvp.otherprofile.OtherProfilePresenter
import com.example.homework2.mvp.otherprofile.OtherProfilePresenterImpl
import com.example.homework2.repositories.UsersRepository
import com.example.homework2.retrofit.RetrofitService
import dagger.Module
import dagger.Provides

@Module
class OtherProfileModule {

    @Provides
    fun provideModelImpl(modelImpl: OtherProfileModelImpl): OtherProfileModel {
        return modelImpl
    }

    @Provides
    fun providePresenterImpl(model: OtherProfileModel): OtherProfilePresenter {
        return OtherProfilePresenterImpl(model = model)
    }

    @Provides
    fun provideUsersRepository(retrofitService: RetrofitService): UsersRepository {
        return UsersRepository(retrofitService = retrofitService)
    }

}
