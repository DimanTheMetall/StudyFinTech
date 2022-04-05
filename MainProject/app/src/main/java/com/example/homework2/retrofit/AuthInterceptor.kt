package com.example.homework2.retrofit

import com.example.homework2.Constance
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder().method(original.method, original.body)
        val authorize = Credentials.basic(
            "kozlovdiman_je@yahoo.com",
            "8gN1Cz944LyqukGE6Uw1t9fzIbqCdRje"
        )
        requestBuilder.header(Constance.AUTHORIZATION_HEADER, authorize)

        return chain.proceed(requestBuilder.build())
    }
}
