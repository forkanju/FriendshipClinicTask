package com.compose.friendship

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.AppInitializer
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.realm.kotlin.internal.RealmInitializer
import javax.inject.Inject

/**
 * Custom Application class for MyApp.
 * Initializes Hilt for dependency injection and sets up WorkManager configuration.
 */
@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * Provides the WorkManager configuration.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setDefaultProcessName(packageName)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory) //Needed for Hilt to work.
            .build()

    /**
     * Called when the application is starting.
     */
    override fun onCreate() {
        super.onCreate()
        // Initialize Realm database, needed for Hilt to work
        AppInitializer.getInstance(this)
            .initializeComponent(RealmInitializer::class.java)
    }
}
