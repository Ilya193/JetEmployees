package ru.ikom.home.presentation

import kotlinx.serialization.Serializable

data class HomeUiState(
    val employeesState: EmployeesUiState = EmployeesUiState.Loading,
    val departments: List<DepartmentUi> = generateDepartments(),
    val showRefresh: Boolean = false,
    val filterState: FilterUiState = FilterUiState(),
    val input: String = "",
    val loadInformationState: LoadInformation = LoadInformation.INIT,
    val nothingFoundState: NothingFound = NothingFound.INIT
)

sealed interface EmployeesUiState {
    data object Loading : EmployeesUiState

    data object Error : EmployeesUiState

    data class Data(
        val employees: List<EmployeeUi>,
        val showRefresh: Boolean = false,
        val nothingFound: NothingFound = NothingFound.INIT
    ) : EmployeesUiState
}

@Serializable
data class FilterUiState(val filter: FilterMode? = null, val show: Boolean = false)

enum class NothingFound {
    INIT,
    SEARCH,
}

enum class LoadInformation {
    INIT,
    LOADING,
    ERROR
}

enum class FilterMode {
    ALPHABET,
    DATE_OF_BIRTH
}