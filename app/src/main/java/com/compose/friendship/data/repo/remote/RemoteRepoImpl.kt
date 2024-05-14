package com.compose.friendship.data.repo.remote

import com.compose.friendship.NetworkHelper
import com.compose.friendship.RequestState
import com.compose.friendship.data.repo.UserRepo
import com.compose.friendship.di.IoDispatcher
import com.compose.friendship.model.UserInfo
import com.compose.friendship.model.UserRealmObject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteRepoImpl @Inject constructor(
    @IoDispatcher private val io: CoroutineDispatcher,
    private val httpClient: HttpClient,
    private val networkHelper: NetworkHelper,
    private val realm: Realm,
    ) : UserRepo {

    override suspend fun create(
        name: String,
        email: String,
        gender: String,
        status: String
    ): RequestState<UserInfo> {
        return withContext(io) {
            try {
                if (!networkHelper.isNetworkConnected()) {
                    return@withContext RequestState.Error("No Internet Connection")
                }
                // @FormUrlEncoded - alternate submitForm
                val result = httpClient.submitForm(
                    url = "public/v2/users",
                    formParameters = parameters {
                        append(name = "name", value = name)
                        append(name = "email", value = email)
                        append(name = "gender", value = gender)
                        append(name = "status", value = status)
                    }
                )
                if (result.status.isSuccess()) {
                    val data = result.body<UserInfo>()
                    RequestState.Success(data)
                } else {
                    RequestState.Error(result.status.value.toString())
                }
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
    ): RequestState<UserInfo> {
        return withContext(io) {
            try {
                if (!networkHelper.isNetworkConnected()) {
                    return@withContext RequestState.Error("No Internet Connection")
                }
                // @FormUrlEncoded - alternate submitForm
                val result = httpClient.submitForm(url = "public/v2/users/$userId",
                    formParameters = parameters {
                        append(name = "name", value = name)
                        append(name = "email", value = email)
                        append(name = "gender", value = gender)
                        append(name = "status", value = status)
                    }) {
                    method = HttpMethod.Patch
                }
                if (result.status.isSuccess()) {
                    val data = result.body<UserInfo>()
                    RequestState.Success(data)
                } else {
                    RequestState.Error(result.status.value.toString())
                }
            } catch (e: Exception) {
                RequestState.Error(e.message.toString())
            }
        }
    }

    override suspend fun getUsers(): RequestState<List<UserInfo>> {
        return withContext(io) {
            try {
                if (!networkHelper.isNetworkConnected()) {
                    return@withContext RequestState.Error("No Internet Connection")
                }
                val result = httpClient.get("public/v2/users")
                if (result.status.isSuccess()) {
                    val data = result.body<List<UserInfo>>()
                    saveUsers(data)
                    RequestState.Success(data)
                } else {
                    RequestState.Error(result.status.value.toString())
                }
            } catch (e: Exception) {
                RequestState.Error(e.message.toString())
            }
        }
    }
    private fun saveUsers(users: List<UserInfo>){
        try {
            users.forEach {remoteUser->
                val localUser = realm.query<UserRealmObject>("id == ${remoteUser.id}").first().find()
                if (localUser == null){ //user not found in local db
                    realm.writeBlocking {
                        copyToRealm(
                            UserRealmObject(
                                id = remoteUser.id,
                                email = remoteUser.email,
                                gender = remoteUser.gender,
                                name = remoteUser.name,
                                status = remoteUser.status
                            )
                        )
                    }
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

}