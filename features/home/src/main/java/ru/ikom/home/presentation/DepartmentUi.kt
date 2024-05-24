package ru.ikom.home.presentation

import kotlinx.serialization.Serializable

@Serializable
data class DepartmentUi(
    val id: Int,
    val name: String,
    val isSelected: Boolean = false,
)