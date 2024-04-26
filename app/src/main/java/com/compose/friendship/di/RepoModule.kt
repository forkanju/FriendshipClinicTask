package com.compose.friendship.di

import com.compose.friendship.data.repo.UserRepo
import com.compose.friendship.data.repo.UserRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepoModule {
    @Binds
    @Singleton
    fun bindMainRepo(impl: UserRepoImpl): UserRepo
}