package com.compose.friendship.di

import com.compose.friendship.data.repo.UserRepo
import com.compose.friendship.data.repo.local.RealmRepoImpl
import com.compose.friendship.data.repo.remote.RemoteRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RealmRepo

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class RemoteRepo

@Module
@InstallIn(SingletonComponent::class)
interface RepoModule {
    @Binds
    @Singleton
    @RealmRepo
    fun bindRealmRepo(impl: RealmRepoImpl): UserRepo

    @Binds
    @Singleton
    @RemoteRepo
    fun bindRemoteRepo(impl: RemoteRepoImpl): UserRepo
}