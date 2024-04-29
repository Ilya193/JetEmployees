package ru.ikom.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class EmployeeDBO(
    val avatarUrl: String,
    val birthday: String,
    val department: String,
    val firstName: String,
    @PrimaryKey
    val id: String,
    val lastName: String,
    val phone: String,
    val position: String,
    val userTag: String
)