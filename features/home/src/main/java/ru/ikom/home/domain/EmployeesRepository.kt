package ru.ikom.home.domain

interface EmployeesRepository {
    suspend fun fetchEmployees(): LoadResult<List<EmployeeDomain>>
}