package ru.ikom.employees

import ru.ikom.database.EmployeeDBO
import ru.ikom.network.EmployeeDTO
import ru.ikom.network.EmployeesDTO


fun EmployeeDTO.toEmployeeData(): EmployeeData =
    EmployeeData(avatarUrl, birthday, department, firstName, id, lastName, phone, position, userTag)

fun EmployeesDTO.toEmployeesData(): List<EmployeeData> =
    items.map { it.toEmployeeData() }

fun EmployeeDBO.toEmployeeData(): EmployeeData =
    EmployeeData(avatarUrl, birthday, department, firstName, id, lastName, phone, position, userTag)

fun EmployeeData.toEmployeeDBO(): EmployeeDBO =
    EmployeeDBO(avatarUrl, birthday, department, firstName, id, lastName, phone, position, userTag)