package ru.ikom.network

import kotlinx.serialization.Serializable

@Serializable
data class EmployeesDTO(
    val items: List<EmployeeDTO>
)