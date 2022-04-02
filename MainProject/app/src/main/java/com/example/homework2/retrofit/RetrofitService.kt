package com.example.homework2.retrofit

import androidx.core.location.LocationRequestCompat
import com.example.homework2.dataclasses.JsonStreams
import com.example.homework2.dataclasses.JsonTopic
import com.example.homework2.dataclasses.JsonUsers
import com.example.homework2.dataclasses.Member
import com.example.homework2.dataclasses.chatdataclasses.Filter
import com.example.homework2.dataclasses.chatdataclasses.JsonMessages
import com.example.homework2.dataclasses.chatdataclasses.Narrow
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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


    @GET("/api/v1/messages")
    @JvmSuppressWildcards
    fun getMessages(
        @Query("narrow") narrow: String,
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int,
        @Query("apply_markdown") apply_markdown: Boolean
    ): Single<JsonMessages>

}
