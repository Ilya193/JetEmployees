package ru.ikom.employees

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.ikom.database.EmployeeDBO
import ru.ikom.database.EmployeesDao

class EmployeesCacheDataSourceImpl(
    private val dao: EmployeesDao
) : EmployeesCacheDataSource {
    override fun fetchEmployees(): Flow<List<EmployeeData>> = dao.fetchEmployees().map {
        it.map { it.toEmployeeData() }
    }

    override suspend fun insertAll(employees: List<EmployeeData>) = dao.insertAll(employees.map { it.toEmployeeDBO() })

    override suspend fun deleteAll() = dao.deleteAll()

}