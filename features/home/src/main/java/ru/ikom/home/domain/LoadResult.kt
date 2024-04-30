package ru.ikom.home.domain

sealed class LoadResult<out T> {

    data class Success<T>(
        val data: T
    ): LoadResult<T>()

    data class Loading<T>(
        val data: T
    ): LoadResult<T>()

    data class Error<T>(
        val data: T,
        val e: ErrorType
    ): LoadResult<T>()

}