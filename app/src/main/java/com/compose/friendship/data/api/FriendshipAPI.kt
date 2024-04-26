package com.compose.friendship.data.api

import com.compose.friendship.model.User
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendshipAPI {

    @FormUrlEncoded
    @POST("public/v2/users")
    suspend fun create(
        @Field("name")   name: String,
        @Field("email")  email: String,
        @Field("gender") gender: String,
        @Field("status") status: String,
    ): Response<User.UserInfo>

    @FormUrlEncoded
    @PATCH("public/v2/users/{userId}")
    suspend fun update(
        @Path("userId") userId: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("gender") gender: String,
        @Field("status") status: String,
    ): Response<User.UserInfo>

    @GET("public/v2/users")
    suspend fun getUsers(
    ): Response<User>
}