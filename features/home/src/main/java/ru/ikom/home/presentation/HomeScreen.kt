package ru.ikom.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val stateTest by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = stateTest.employeesState) {
        is EmployeesUiState.Loading -> LoadingEmployees(stateTest.departments)
        is EmployeesUiState.Error -> Error { viewModel.action(Event.Fetch) }
        is EmployeesUiState.Data -> EmployeesData(
            state = state,
            departments = stateTest.departments,
            refreshState = stateTest.showRefresh,
            inputState = stateTest.input,
            dataLoadState = stateTest.loadInformationState,
            selectDepartment = { viewModel.action(Event.SelectDepartments(it)) },
            refresh = { viewModel.action(Event.Refresh) },
            launchDialog = { viewModel.action(Event.Filter(stateTest.filterState.filter, true)) },
            input = { viewModel.action(Event.Input(it)) },
            cancel = { viewModel.action(Event.Cancel) },
            onClick = { viewModel.action(Event.OpenDetails(it)) })
    }

    if (stateTest.filterState.show) {
        ChoiseModalBottomSheet(stateTest.filterState.filter) {
            viewModel.action(Event.Filter(it, false))
        }
    }
}