package com.compose.friendship.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.compose.friendship.Constants
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import javax.inject.Singleton

/**by @forkan at pran-rfl group-2024*/
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        return ChuckerInterceptor(context = context)
    }

    @Singleton
    @Provides
    fun provideKtorClient(
        chuckerInterceptor: ChuckerInterceptor
    ): HttpClient {
        val okHttpEngine = OkHttp.create {
            addInterceptor(chuckerInterceptor)
        }
        return HttpClient(okHttpEngine) {
            install(Logging) {
                level = LogLevel.BODY
            }
            defaultRequest {
                url(Constants.BASE_URL)
                header(HttpHeaders.Authorization, Constants.TOKEN)
                header(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json
                )//not needed , but for understanding
                //alternate way
                /**     headers {
                append(HttpHeaders.Authorization, Constants.TOKEN)
                append(HttpHeaders.ContentType, ContentType.Application.Json) //not needed , but for understanding
                }*/
            }
            install(ContentNegotiation) {
                //for kotlinx and gson serialization
                /**             json(Json {
                explicitNulls = false
                encodeDefaults = true
                })
                gson{
                serializeNulls()
                }*/
                //for jackson serialization-recommended
                jackson {
                    setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP))
                }
            }
        }
    }

}
