package com.compose.friendship.data.repo

import com.compose.friendship.RequestState
import com.compose.friendship.model.UserInfo

interface UserRepo {

    suspend fun create(
        name: String,
        email: String,
        gender: String,
        status: String
    ): RequestState<UserInfo>

    suspend fun update(
        userId: String,
        name: String,
        email: String,
        gender: String,
        status: String
    ): RequestState<UserInfo>

    suspend fun getUsers(): RequestState<List<UserInfo>>
}