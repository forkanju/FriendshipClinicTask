package com.compose.friendship.data.repo

import android.util.Log
import com.compose.friendship.RequestState
import com.compose.friendship.data.api.FriendshipAPI
import com.compose.friendship.di.IoDispatcher
import com.compose.friendship.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    @IoDispatcher private val io: CoroutineDispatcher,
    private val api: FriendshipAPI
) : UserRepo {
    override suspend fun create(
        name: String,
        email: String,
        gender: String,
        status: String
    ): RequestState<User.UserInfo> {
        return withContext(io) {
            try {
                val result = api.create(
                    name = name,
                    email = email,
                    gender = gender,
                    status = status
                )
                val data = result.body()
                if (result.isSuccessful && data != null)
                    RequestState.Success(data)
                else
                    RequestState.Error("Field is empty!")
            } catch (e: UnknownHostException) {
                RequestState.Error("No Internet")
            } catch (e: IOException) {
                RequestState.Error("Server Not Responding")
            } catch (e: Exception) {
                RequestState.Error(e.message.toString())
            }
        }
    }

    override suspend fun update(
        userId: String,
        name: String,
        email: String,
        gender: String,
        status: String
    ): RequestState<User.UserInfo> {
        return withContext(io) {
            try {
                val result = api.update(
                    userId = userId,
                    name = name,
                    email = email,
                    gender = gender,
                    status = status
                )
                val data = result.body()
                if (result.isSuccessful && data != null){
                    Log.d("UserRepoImpl", "data: $data")
                    if (result.code() == 200) {
                        Log.d("UserRepoImpl", "code: ${result.code()}")

                        RequestState.Success(data)
                    } else {
                        Log.d("UserRepoImpl", "code: ${result.code()}")
                        RequestState.Error("Unexpected status code: ${result.code()}")
                    }
                } else{
                    Log.d("UserRepoImpl", "code e : ${result.code()}")
                    Log.d("UserRepoImpl", "code e : ${result.message()}")
                    RequestState.Error("Field is empty!")

                }
            } catch (e: UnknownHostException) {
                Log.d("UserRepoImpl", "e unknown: ${e.message}")
                RequestState.Error("No Internet")
            } catch (e: IOException) {
                Log.d("UserRepoImpl", "io: ${e.message}")
                RequestState.Error("Server Not Responding")
            } catch (e: Exception) {
                Log.d("UserRepoImpl", "Ex: ${e.message}")
                RequestState.Error(e.message.toString())
            }
        }
    }

    override suspend fun getUsers(): RequestState<List<User.UserInfo>> {
        return withContext(io) {
            try {
                val result = api.getUsers()
                val data = result.body()
                if (result.isSuccessful && data != null)
                    RequestState.Success(data)
                else
                    RequestState.Error(result.message())
            } catch (e: UnknownHostException) {
                RequestState.Error("No Internet")
            } catch (e: IOException) {
                RequestState.Error("Server Not Responding")
            } catch (e: Exception) {
                RequestState.Error(e.message.toString())
            }
        }
    }
}