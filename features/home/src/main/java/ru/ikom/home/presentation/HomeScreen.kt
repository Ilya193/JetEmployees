package ru.ikom.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val employeesState by viewModel.employeesUiState.collectAsStateWithLifecycle()
    val departmentsUiState by viewModel.departmentsUiState.collectAsStateWithLifecycle()
    val refreshUiState by viewModel.refreshUiState.collectAsStateWithLifecycle()
    val filterUiState by viewModel.filterUiState.collectAsStateWithLifecycle()
    val inputUiState by viewModel.inputUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.action(Event.Fetch)
    }

    when (val state = employeesState) {
        is EmployeesUiState.Loading -> LoadingEmployees(departmentsUiState.departments)
        is EmployeesUiState.Error -> Error { viewModel.action(Event.Fetch) }
        is EmployeesUiState.Data -> EmployeesData(
            state = state,
            departments = departmentsUiState.departments,
            refreshState = refreshUiState.show,
            inputState = inputUiState.input,
            selectDepartment = { viewModel.action(Event.SelectDepartments(it)) },
            refresh = { viewModel.action(Event.Refresh) },
            launchDialog = { viewModel.action(Event.Filter(filterUiState.filter, true)) },
            input = { viewModel.action(Event.Input(it)) },
            cancel = { viewModel.action(Event.Cancel) })
    }

    if (filterUiState.show) {
        ChoiseModalBottomSheet(filterUiState.filter) {
            viewModel.action(Event.Filter(it, false))
        }
    }
}