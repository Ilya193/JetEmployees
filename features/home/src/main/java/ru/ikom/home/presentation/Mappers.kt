package ru.ikom.home.presentation

import ru.ikom.home.domain.EmployeeDomain

fun EmployeeDomain.toEmployeeUi(): EmployeeUi =
    EmployeeUi(avatarUrl, birthday, department, firstName, id, lastName, phone, position, userTag)