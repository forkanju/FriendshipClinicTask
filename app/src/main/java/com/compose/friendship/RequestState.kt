package com.compose.friendship

sealed class RequestState<out T> {
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val error: String, val code: Int = -1) : RequestState<Nothing>()
}
