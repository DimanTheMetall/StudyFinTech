package com.example.homework2.retrofit

import com.example.homework2.dataclasses.JsonStreams
import com.example.homework2.dataclasses.JsonTopic
import com.example.homework2.dataclasses.JsonUsers
import com.example.homework2.dataclasses.Member
import com.example.homework2.dataclasses.chatdataclasses.*
import io.reactivex.Single
import retrofit2.http.*

interface RetrofitService {

    //Функция для обновления сообщения после добавления реакции
    @GET("/api/v1/messages/{msg_id}")
    fun getOneMessage(@Path("msg_id") messageId: Long): Single<SelectViewTypeClass.Chat.Message>

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
        @Query("narrow") narrow: Any,
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int,
        @Query("apply_markdown") apply_markdown: Boolean
    ): Single<JsonMessages>

    @POST("/api/v1/messages")
    fun sendMessage(
        @Query("type") type: String,
        @Query("to") to: String,
        @Query("content") content: String,
        @Query("topic") topic: String
    ): Single<ResponseFromSendMessage>

    @GET("/api/v1/users/{user_id_or_email}/presence")
    fun getPresence(@Path("user_id_or_email") user_id_or_email: Any): Single<JsonPresense>

    @POST("/api/v1/messages/{message_id}/reactions")
    fun addEmoji(
        @Path("message_id") message_id: Long,
        @Query("emoji_name") emoji_name: String,
        @Query("reaction_type") reaction_type: String,
        @Query("emoji_code") emoji_code: String?
    ): Single<JsonRespone>

    @DELETE("/api/v1/messages/{message_id}/reactions")
    fun deleteEmoji(
        @Path("message_id") message_id: Long,
        @Query("emoji_name") emoji_name: String,
        @Query("reaction_type") reaction_type: String,
    ): Single<JsonRespone>

}
