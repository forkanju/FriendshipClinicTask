package com.compose.friendship.worker

import android.content.ComponentName
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.multiprocess.RemoteCoroutineWorker
import androidx.work.multiprocess.RemoteWorkerService
import androidx.work.workDataOf
import com.compose.friendship.BuildConfig
import com.compose.friendship.RequestState
import com.compose.friendship.data.repo.UserRepo
import com.compose.friendship.di.RemoteRepo
import com.compose.friendship.model.UserRealmObject
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import org.mongodb.kbson.ObjectId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltWorker
class DataUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : RemoteCoroutineWorker(context, params) {

    companion object {
        const val ID = "realmObjectId" // Key for storing Realm object ID in work data
        const val TYPE = "work_type" // Key for storing work type in work data

        /**
         * Builds a OneTimeWorkRequest for DataUpdateWorker.
         * @param context The context to use.
         * @param realmObjectId The ObjectId of the Realm object.
         * @param workType The type of work to perform.
         */
        fun buildOneTimeWorkRequest(context: Context, realmObjectId: ObjectId, workType: Type) {
            // Create a component name for the remote worker service
            val serviceName = RemoteWorkerService::class.java.name
            val componentName = ComponentName(BuildConfig.APPLICATION_ID, serviceName)

            // Create work data containing necessary information
            val data = workDataOf(
                ARGUMENT_PACKAGE_NAME to componentName.packageName,
                ARGUMENT_CLASS_NAME to componentName.className,
                ID to realmObjectId.toHexString(),
                TYPE to workType.name
            )

            // Build constraints for the work request
            val constraints = Constraints.Builder().apply {
                setRequiredNetworkType(NetworkType.CONNECTED)
            }.build()

            // Build the OneTimeWorkRequest
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DataUpdateWorker::class.java)
                .setInputData(data)
                // Set a delay before starting the work
                .setInitialDelay(5, TimeUnit.SECONDS)
                // Set backoff criteria for retrying
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30000, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            // Enqueue the work request with WorkManager
            WorkManager
                .getInstance(context)
                .enqueue(oneTimeWorkRequest)
        }
    }

    @Inject
    lateinit var realm: Realm

    @Inject
    @RemoteRepo
    lateinit var repo: UserRepo

    /**
     * Performs the actual work in a remote coroutine.
     */
    override suspend fun doRemoteWork(): Result {
        // Extract work type and Realm object ID from input data
        val workType = inputData.getString(TYPE)
        val realmObjectHex = inputData.getString(ID)
            ?: return Result.failure(workDataOf("message" to "RealmObjectId is null"))
        val realmObjectId = ObjectId(realmObjectHex)

        // Query Realm for the specified object
        val realmObject = realm.query<UserRealmObject>("_id == $0", realmObjectId).first().find()
            ?: return Result.failure(workDataOf("message" to "RealmObject is null"))

        // Perform the appropriate action based on work type
        val result = when (workType) {
            Type.UPDATE.name -> {
                repo.update(
                    realmObject.id.toString(),
                    realmObject.name,
                    realmObject.email,
                    realmObject.gender,
                    realmObject.status
                )
            }

            Type.CREATE.name -> {
                repo.create(
                    realmObject.name,
                    realmObject.email,
                    realmObject.gender,
                    realmObject.status
                )
            }

            else -> return Result.failure(workDataOf("message" to "Unknown work type"))
        }

        // Handle the result of the repository operation
        return when (result) {
            is RequestState.Error ->
                Result.failure(workDataOf("message" to result.error))

            is RequestState.Success ->
                Result.success(workDataOf("message" to "Data Updated Successfully"))

            is RequestState.Loading -> return Result.retry()
        }
    }

    // Enumeration representing the type of work
    enum class Type {
        UPDATE, CREATE
    }

}
