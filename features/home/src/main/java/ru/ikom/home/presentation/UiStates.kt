package ru.ikom.home.presentation

sealed interface EmployeesUiState {
    data object Loading : EmployeesUiState

    data object Error : EmployeesUiState

    data class Data(
        val employees: List<EmployeeUi>,
        val showRefresh: Boolean = false,
        val nothingFound: NothingFound = NothingFound.INIT
    ) : EmployeesUiState
}

data class DepartmentsUiState(val departments: List<DepartmentUi> = generateDepartments())

data class RefreshUiState(val show: Boolean = false)

data class FilterUiState(val filter: FilterMode? = null, val show: Boolean = false)

data class InputUiState(val input: String = "")

data class DataLoadInformationState(val state: LoadInformation? = null)

enum class NothingFound {
    INIT,
    SEARCH,
}

enum class LoadInformation {
    LOADING,
    ERROR
}

enum class FilterMode {
    ALPHABET,
    DATE_OF_BIRTH
}