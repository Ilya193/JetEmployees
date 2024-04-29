package ru.ikom.employees

import kotlinx.coroutines.flow.Flow

interface EmployeesCacheDataSource {
    fun fetchEmployees(): Flow<List<EmployeeData>>
    suspend fun insertAll(employees: List<EmployeeData>)
    suspend fun deleteAll()
}