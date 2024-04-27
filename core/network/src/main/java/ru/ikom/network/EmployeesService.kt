package ru.ikom.network

import retrofit2.http.GET

interface EmployeesService {

    @GET("mocks/kode-api/trainee-test/331141861/users")
    suspend fun fetchEmployees(): EmployeesDTO
}