package ru.ikom.home.domain

sealed class LoadResult<out T> {

    data class Success<T>(
        val data: T
    ): LoadResult<T>()

    data class Error(
        val e: ErrorType
    ): LoadResult<Nothing>()

}