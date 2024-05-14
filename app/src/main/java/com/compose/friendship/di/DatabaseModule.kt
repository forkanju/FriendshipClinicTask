package com.compose.friendship.di

import com.compose.friendship.model.UserRealmObject
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(): Realm {
        val config = RealmConfiguration.Builder(setOf(UserRealmObject::class))
            .name("test.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        return Realm.open(config)
    }

}