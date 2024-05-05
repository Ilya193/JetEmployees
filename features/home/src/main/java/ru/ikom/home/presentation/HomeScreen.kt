package ru.ikom.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(component: HomeComponent) {
    val values by component.state.collectAsStateWithLifecycle()

    when (val state = values.employeesState) {
        is EmployeesUiState.Loading -> LoadingEmployees(values.departments)
        is EmployeesUiState.Error -> Error { component.action(Event.Fetch) }
        is EmployeesUiState.Data -> EmployeesData(
            state = state,
            departments = values.departments,
            refreshState = values.showRefresh,
            inputState = values.input,
            dataLoadState = values.loadInformationState,
            selectDepartment = { component.action(Event.SelectDepartments(it)) },
            refresh = { component.action(Event.Refresh) },
            launchDialog = { component.action(Event.Filter(values.filterState.filter, true)) },
            input = { component.action(Event.Input(it)) },
            cancel = { component.action(Event.Cancel) },
            onClick = { component.action(Event.OpenDetails(it)) })
    }

    if (values.filterState.show) {
        ChoiseModalBottomSheet(values.filterState.filter) {
            component.action(Event.Filter(it, false))
        }
    }
}