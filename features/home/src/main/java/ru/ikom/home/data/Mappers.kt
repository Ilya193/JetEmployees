package ru.ikom.home.data

import ru.ikom.employees.EmployeeData
import ru.ikom.home.domain.EmployeeDomain

fun EmployeeData.toEmployeeDomain(): EmployeeDomain =
    EmployeeDomain(avatarUrl, birthday, department, firstName, id, lastName, phone, position, userTag)