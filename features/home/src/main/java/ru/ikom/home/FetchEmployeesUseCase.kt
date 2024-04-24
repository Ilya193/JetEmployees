package ru.ikom.home

import ru.ikom.employees.EmployeesCloudDataSource

class FetchEmployeesUseCase(
    private val dataSource: EmployeesCloudDataSource
) {

    suspend operator fun invoke(): List<EmployeeUi> =
        dataSource.fetchEmployees().map { it.toEmployeeUi() }
}