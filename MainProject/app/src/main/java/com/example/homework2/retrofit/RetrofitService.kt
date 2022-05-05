package com.example.homework2.retrofit

import com.example.homework2.dataclasses.chatdataclasses.*
import com.example.homework2.dataclasses.streamsandtopics.JsonStreams
import com.example.homework2.dataclasses.streamsandtopics.JsonTopic
import com.example.homework2.dataclasses.streamsandtopics.JsonUsers
import com.example.homework2.dataclasses.streamsandtopics.Member
import io.reactivex.Single
import retrofit2.http.*

interface RetrofitService {

    //Функция для обновления сообщения после добавления реакции
    @GET("messages/{msg_id}")
    fun getOneMessage(
        @Path("msg_id") messageId: Long,
        @Query("apply_markdown") apply_markdown: Boolean
    ): Single<JsonMessage>

    @GET("streams")
    fun getAllStreams(): Single<JsonStreams>

    @GET("users/me/{stream_id}/topics")
    fun getTopicList(@Path("stream_id") streamId: Int): Single<JsonTopic>

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Single<JsonStreams>

    @GET("users")
    fun getUsers(): Single<JsonUsers>

    @GET("users/me")
    fun getOwnUser(): Single<Member>

    @GET("messages")
    @JvmSuppressWildcards
    fun getMessages(
        @Query("narrow") narrow: Any,
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int,
        @Query("apply_markdown") apply_markdown: Boolean
    ): Single<JsonMessages>

    @POST("messages")
    fun sendMessage(
        @Query("type") type: String,
        @Query("to") to: String,
        @Query("content") content: String,
        @Query("topic") topic: String
    ): Single<ResponseFromSendMessage>

    @GET("users/{user_id_or_email}/presence")
    fun getPresence(@Path("user_id_or_email") user_id_or_email: Any): Single<JsonPresense>

    @POST("messages/{message_id}/reactions")
    fun addEmoji(
        @Path("message_id") message_id: Long,
        @Query("emoji_name") emoji_name: String,
        @Query("reaction_type") reaction_type: String,
        @Query("emoji_code") emoji_code: String?
    ): Single<JsonResponse>

    @DELETE("messages/{message_id}/reactions")
    fun deleteEmoji(
        @Path("message_id") message_id: Long,
        @Query("emoji_name") emoji_name: String,
        @Query("reaction_type") reaction_type: String,
    ): Single<JsonResponse>

    @POST("users/me/subscriptions")
    fun createOrSubscribeStream(@Query("subscriptions") subscriptions: String): Single<JsonResponse>

}
