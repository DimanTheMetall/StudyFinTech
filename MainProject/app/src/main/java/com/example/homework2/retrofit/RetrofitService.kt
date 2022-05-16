package com.example.homework2.retrofit

import com.example.homework2.dataclasses.chatdataclasses.ResultMessage
import com.example.homework2.dataclasses.chatdataclasses.ResultMessages
import com.example.homework2.dataclasses.chatdataclasses.ResultPresence
import com.example.homework2.dataclasses.chatdataclasses.ResultResponse
import com.example.homework2.dataclasses.streamsandtopics.Member
import com.example.homework2.dataclasses.streamsandtopics.ResultStreams
import com.example.homework2.dataclasses.streamsandtopics.ResultTopic
import com.example.homework2.dataclasses.streamsandtopics.ResultUsers
import io.reactivex.Single
import retrofit2.http.*

interface RetrofitService {

    //Функция для обновления сообщения после добавления реакции
    @GET("messages/{msg_id}")
    fun getOneMessage(
        @Path("msg_id") messageId: Long,
        @Query("apply_markdown") apply_markdown: Boolean
    ): Single<ResultMessage>

    @GET("streams")
    fun getAllStreams(): Single<ResultStreams>

    @GET("users/me/{stream_id}/topics")
    fun getTopicList(@Path("stream_id") streamId: Int): Single<ResultTopic>

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Single<ResultStreams>

    @GET("users")
    fun getUsers(): Single<ResultUsers>

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
    ): Single<ResultMessages>

    @POST("messages")
    fun sendMessageInTopic(
        @Query("type") type: String,
        @Query("to") to: String,
        @Query("content") content: String,
        @Query("topic") topic: String
    ): Single<ResultResponse>

    @GET("users/{user_id_or_email}/presence")
    fun getPresence(@Path("user_id_or_email") user_id_or_email: Any): Single<ResultPresence>

    @POST("messages/{message_id}/reactions")
    fun addEmoji(
        @Path("message_id") message_id: Long,
        @Query("emoji_name") emoji_name: String,
        @Query("reaction_type") reaction_type: String,
        @Query("emoji_code") emoji_code: String?
    ): Single<ResultResponse>

    @DELETE("messages/{message_id}/reactions")
    fun deleteEmoji(
        @Path("message_id") message_id: Long,
        @Query("emoji_name") emoji_name: String,
        @Query("reaction_type") reaction_type: String,
    ): Single<ResultResponse>

    @POST("users/me/subscriptions")
    fun createOrSubscribeStream(@Query("subscriptions") subscriptions: String): Single<ResultResponse>

    @DELETE("messages/{msg_id}")
    fun deleteMessageById(@Path("msg_id") msg_id: Long): Single<ResultResponse>

    @PATCH("messages/{msg_id}")
    fun editMessage(
        @Path("msg_id") msg_id: Long,
        @Query("topic") topic: String,
        @Query("content") content: String
    ): Single<ResultResponse>

}
