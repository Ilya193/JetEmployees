package ru.ikom.home.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import ru.ikom.employees.EmployeesCacheDataSource
import ru.ikom.employees.EmployeesCloudDataSource
import ru.ikom.home.domain.EmployeeDomain
import ru.ikom.home.domain.EmployeesRepository
import ru.ikom.home.domain.ErrorType
import ru.ikom.home.domain.LoadResult
import java.net.UnknownHostException

class EmployeesRepositoryImpl(
    private val cloudDataSource: EmployeesCloudDataSource,
    private val cacheDataSource: EmployeesCacheDataSource,
) : EmployeesRepository {
    override fun fetchEmployees(): Flow<LoadResult<List<EmployeeDomain>>> {
        var employees = emptyList<EmployeeDomain>()
        var isLoading = true
        val cacheFlow: Flow<LoadResult<List<EmployeeDomain>>> = flow {
            cacheDataSource.fetchEmployees().map { it.map { it.toEmployeeDomain() } }.collect {
                if (it.isNotEmpty()) {
                    employees = it.toList()
                    if (isLoading) emit(LoadResult.Loading(employees))
                    else emit(LoadResult.Success(employees))
                }
            }
        }
        val cloudFlow: Flow<LoadResult<List<EmployeeDomain>>> = flow {
            try {
                emit(LoadResult.Loading(employees))
                val employeesData = cloudDataSource.fetchEmployees()
                cacheDataSource.deleteAll()
                cacheDataSource.insertAll(employeesData)
                isLoading = false
            } catch (e: UnknownHostException) {
                emit(LoadResult.Error(employees, ErrorType.NO_CONNECTION))
            } catch (e: Exception) {
                emit(LoadResult.Error(emptyList(), ErrorType.GENERIC_ERROR))
            }
        }

        return merge(cacheFlow, cloudFlow)
    }
}