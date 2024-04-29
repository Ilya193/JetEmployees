package ru.ikom.home.domain

data class LoadResult<T>(val data: T, val loading: Boolean = false, val error: ErrorType? = null)