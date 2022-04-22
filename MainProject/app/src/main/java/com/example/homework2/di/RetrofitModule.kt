package com.example.homework2.di

import com.example.homework2.Constance
import com.example.homework2.retrofit.AuthInterceptor
import com.example.homework2.retrofit.RetrofitService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class RetrofitModule {

    @Provides
    fun providesInterceptor(): OkHttpClient {
        val httpLoginInterceptor = HttpLoggingInterceptor()
        httpLoginInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(httpLoginInterceptor)
            .addInterceptor(AuthInterceptor())
            .build()
    }

    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): RetrofitService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constance.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        return retrofit.create(RetrofitService::class.java)
    }
}