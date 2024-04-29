package ru.ikom.employees

import ru.ikom.network.EmployeesService

class EmployeesCloudDataSourceImpl(
    private val service: EmployeesService
) : EmployeesCloudDataSource {
    override suspend fun fetchEmployees(): List<EmployeeData> =
        service.fetchEmployees().toEmployeesData()
}