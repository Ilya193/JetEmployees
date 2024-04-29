package ru.ikom.home.presentation

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeUi(
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