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

class EmployeesRepositoryImplTest(
    private val cloudDataSource: EmployeesCloudDataSource,
    private val cacheDataSource: EmployeesCacheDataSource,
) : EmployeesRepository {
    override fun fetchEmployees(): Flow<LoadResult<List<EmployeeDomain>>> {
        var state = LoadResult<List<EmployeeDomain>>(data = emptyList())
        val cacheFlow: Flow<LoadResult<List<EmployeeDomain>>> = flow {
            cacheDataSource.fetchEmployees().map { it.map { it.toEmployeeDomain() } }.collect {
                state = state.copy(data = it)
                if (state.data.isNotEmpty()) emit(state)
            }
        }
        val cloudFlow: Flow<LoadResult<List<EmployeeDomain>>> = flow {
            try {
                state = state.copy(loading = true, error = null)
                emit(state)
                val employeesData = cloudDataSource.fetchEmployees()
                cacheDataSource.deleteAll()
                cacheDataSource.insertAll(employeesData)
                state = state.copy(loading = false, error = null)
                emit(state)
            } catch (e: UnknownHostException) {
                emit(state.copy(loading = false, error = ErrorType.NO_CONNECTION))
            } catch (e: Exception) {
                emit(state.copy(data = emptyList(), loading = false, error = ErrorType.NO_CONNECTION))
            }
        }

        return merge(cacheFlow, cloudFlow)
    }
}