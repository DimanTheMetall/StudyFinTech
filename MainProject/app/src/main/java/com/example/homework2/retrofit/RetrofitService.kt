package com.example.homework2.retrofit

import androidx.core.location.LocationRequestCompat
import com.example.homework2.dataclasses.JsonStreams
import com.example.homework2.dataclasses.JsonTopic
import com.example.homework2.dataclasses.JsonUsers
import com.example.homework2.dataclasses.Member
import com.example.homework2.dataclasses.chatdataclasses.*
import io.reactivex.Single
import retrofit2.http.*

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

    @POST("/api/v1/messages")
    fun sendMessage(@Query ("type") type: String,
    @Query("to") to: String,
    @Query("content") content: String,
    @Query("topic") topic : String): Single<ResponseFromSendMessage>

    @GET("/api/v1/users/{user_id_or_email}/presence")
    fun getPresense(@Path("user_id_or_email") user_id_or_email: Any): Single<JsonPresense>

}
