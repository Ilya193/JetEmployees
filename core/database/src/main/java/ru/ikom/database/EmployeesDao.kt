package ru.ikom.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeesDao {
    @Query("SELECT * FROM employees")
    fun fetchEmployees(): Flow<List<EmployeeDBO>>

    @Insert
    suspend fun insertAll(employees: List<EmployeeDBO>)

    @Query("DELETE FROM employees")
    suspend fun deleteAll()
}