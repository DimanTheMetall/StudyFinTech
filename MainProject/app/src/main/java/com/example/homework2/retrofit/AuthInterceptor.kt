package com.example.homework2.retrofit

import com.example.homework2.BuildConfig
import com.example.homework2.Constants
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder().method(original.method, original.body)
        val authorize = Credentials.basic(
            BuildConfig.ZULIP_EMAIL,
            BuildConfig.ZULIP_APIKEY
        )
        requestBuilder.header(Constants.AUTHORIZATION_HEADER, authorize)

        return chain.proceed(requestBuilder.build())
    }
}
