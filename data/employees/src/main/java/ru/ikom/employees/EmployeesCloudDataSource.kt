package ru.ikom.employees

interface EmployeesCloudDataSource {
    suspend fun fetchEmployees(): List<EmployeeData>
}