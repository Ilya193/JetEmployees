package ru.ikom.home.data

import ru.ikom.employees.EmployeesCloudDataSource
import ru.ikom.home.domain.EmployeeDomain
import ru.ikom.home.domain.EmployeesRepository
import ru.ikom.home.domain.ErrorType
import ru.ikom.home.domain.LoadResult
import java.net.UnknownHostException

class EmployeesRepositoryImpl(
    private val dataSource: EmployeesCloudDataSource
) : EmployeesRepository {
    override suspend fun fetchEmployees(): LoadResult<List<EmployeeDomain>> {
        return try {
            val employeesData = dataSource.fetchEmployees()
            LoadResult.Success(employeesData.map { it.toEmployeeDomain() })
        } catch (e: UnknownHostException) {
            LoadResult.Error(ErrorType.NO_CONNECTION)
        } catch (e: Exception) {
            LoadResult.Error(ErrorType.GENERIC_ERROR)
        }
    }
}