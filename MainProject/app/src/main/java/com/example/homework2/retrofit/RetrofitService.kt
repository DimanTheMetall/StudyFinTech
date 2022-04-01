package com.example.homework2.retrofit

import com.example.homework2.dataclasses.JsonStreams
import com.example.homework2.dataclasses.JsonTopic
import com.example.homework2.dataclasses.JsonUsers
import com.example.homework2.dataclasses.Member
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitService {

    @GET("/api/v1/streams")
    fun getAllStreams(): Single<JsonStreams>

    @GET("/api/v1/users/me/{stream_id}/topics")
    fun getTopicList(@Path("stream_id") streamId: Int): Single<JsonTopic>

    @GET("/api/v1/users/me/subscriptions")
    fun getSubscribedStreams(): Single<JsonStreams>

    @GET("/api/v1/users")
    fun getUsers(): Single<JsonUsers>

    @GET("/api/v1/users/me")
    fun getOwnUser(): Single<Member>

}
