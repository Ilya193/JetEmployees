package ru.ikom.network

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDTO(
    val avatarUrl: String,
    val birthday: String,
    val department: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    val phone: String,
    val position: String,
    val userTag: String
)