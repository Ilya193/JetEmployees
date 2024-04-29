package ru.ikom.home.domain

import kotlinx.coroutines.flow.Flow

interface EmployeesRepository {
    fun fetchEmployees(): Flow<LoadResult<List<EmployeeDomain>>>
}