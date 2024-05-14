package com.compose.friendship.data.repo.local

import android.content.Context
import com.compose.friendship.RequestState
import com.compose.friendship.data.repo.UserRepo
import com.compose.friendship.di.IoDispatcher
import com.compose.friendship.model.UserInfo
import com.compose.friendship.model.UserRealmObject
import com.compose.friendship.worker.DataUpdateWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealmRepoImpl @Inject constructor(
    @IoDispatcher private val io: CoroutineDispatcher,
    private val realm: Realm,
    @ApplicationContext private val context: Context
    ) : UserRepo {

    override suspend fun create(
        name: String,
        email: String,
        gender: String,
        status: String
    ): RequestState<UserInfo> {
        return withContext(io) {
            try {
                val user = realm.writeBlocking {
                    copyToRealm(
                        UserRealmObject(
                            email = email,
                            gender = gender,
                            name = name,
                            status = status
                        )
                    )
                }
                DataUpdateWorker.buildOneTimeWorkRequest(context, user._id, DataUpdateWorker.Type.CREATE)
                RequestState.Success(user.toUserInfo())
            } catch (e: Exception) {
                RequestState.Error(e.message ?: "Unknown Error")
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
                realm.writeBlocking {
                    val user = query<UserRealmObject>("id == $userId").first().find()
                    if(user != null) {
                        user.name = name
                        user.email = email
                        user.gender = gender
                        user.status = status
                        DataUpdateWorker.buildOneTimeWorkRequest(context, user._id, DataUpdateWorker.Type.UPDATE)
                        RequestState.Success(user.toUserInfo())
                    }
                    else
                        RequestState.Error("User not found")
                }
            } catch (e: Exception) {
                RequestState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    override suspend fun getUsers(): RequestState<List<UserInfo>> {
        return withContext(io) {
            try {
                val users = realm.query<UserRealmObject>().find()
                RequestState.Success(users.map { it.toUserInfo() })
            } catch (e: Exception) {
                RequestState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}