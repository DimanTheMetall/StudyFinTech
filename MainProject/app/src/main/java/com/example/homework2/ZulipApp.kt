package com.example.homework2

import android.app.Activity
import android.app.Application
import androidx.fragment.app.FragmentActivity
import com.example.homework2.di.DaggerZulipComponent
import com.example.homework2.di.ZulipComponent
import com.example.homework2.retrofit.AuthInterceptor
import com.example.homework2.retrofit.RetrofitService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ZulipApp : Application() {


    lateinit var retrofitService: RetrofitService

    lateinit var zulipComponent: ZulipComponent

    override fun onCreate() {
        super.onCreate()
        configureRetrofit()
        zulipComponent =
            DaggerZulipComponent.factory().create(applicationContext)
    }

    private fun configureRetrofit() {

        val httpLoginInterceptor = HttpLoggingInterceptor()
        httpLoginInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoginInterceptor)
            .addInterceptor(AuthInterceptor())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constance.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        retrofitService = retrofit.create(RetrofitService::class.java)
    }
}

fun FragmentActivity.zulipApp(): ZulipApp {
    return this.application as ZulipApp
}

fun Activity.zulipApp(): ZulipApp {
    return this.application as ZulipApp
}
