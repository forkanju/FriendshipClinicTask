package com.compose.friendship

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.AppInitializer
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.realm.kotlin.internal.RealmInitializer
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setDefaultProcessName(packageName)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        AppInitializer
            .getInstance(this)
            .initializeComponent(RealmInitializer::class.java)
    }
}