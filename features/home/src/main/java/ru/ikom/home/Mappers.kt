package ru.ikom.home

import ru.ikom.employees.EmployeeData

fun EmployeeData.toEmployeeUi(): EmployeeUi =
    EmployeeUi(avatarUrl, birthday, department, firstName, id, lastName, phone, position, userTag)