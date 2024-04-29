package ru.ikom.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EmployeeDBO::class], version = 1)
abstract class EmployeesDb : RoomDatabase() {
    abstract fun employeesDao(): EmployeesDao
}