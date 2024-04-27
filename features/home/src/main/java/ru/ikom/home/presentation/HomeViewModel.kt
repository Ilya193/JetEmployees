package ru.ikom.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ikom.home.domain.EmployeesRepository
import ru.ikom.home.domain.LoadResult

class HomeViewModel(
    private val repository: EmployeesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private var employees = mutableListOf<EmployeeUi>()
    private var showEmployees = mutableListOf<EmployeeUi>()
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> get() = _uiState

    fun action(event: Event) = viewModelScope.launch(dispatcher) {
        when (event) {
            is Event.Fetch -> fetchEmployees()
            is Event.SelectCategory -> selectCategory(event.index)
            is Event.Refresh -> refresh()
            is Event.Input -> input(event.value)
            is Event.Cancel -> cancel()
            is Event.Dialog -> dialog(event.show)
            is Event.Filter -> filter(event.value)
        }
    }

    private fun fetchEmployees() = viewModelScope.launch(dispatcher) {
        when (val result = repository.fetchEmployees()) {
            is LoadResult.Success -> {
                employees = result.data.map { it.toEmployeeUi() }.toMutableList()
                showEmployees = employees.toMutableList()
                _uiState.value = HomeUiState(employees = showEmployees.toList())
            }

            is LoadResult.Error -> _uiState.value = HomeUiState(isError = true)
        }
    }

    private fun refresh() = viewModelScope.launch(dispatcher) {
        _uiState.update { it.copy(showRefresh = true) }
        fetchEmployees()
    }

    private fun selectCategory(index: Int) = viewModelScope.launch(dispatcher) {
        val departments = _uiState.value.departments.toMutableList()
        val item = departments[index]
        showEmployees.clear()

        if (!item.isSelected) {
            if (index == 0) {
                showEmployees = employees.toMutableList()
                _uiState.value = HomeUiState(employees = employees.toList())
            } else {
                for (i in departments.indices) {
                    if (departments[i] == item) departments[i] = departments[i].copy(isSelected = true)
                    else departments[i] = departments[i].copy(isSelected = false)
                }

                val word = item.title.lowercase()
                employees.forEach { employee ->
                    if (word in employee.department) showEmployees.add(employee)
                }
                _uiState.value = HomeUiState(
                    employees = showEmployees.toList(),
                    departments = departments.toList()
                )
            }
        }
    }

    private fun input(value: String) = viewModelScope.launch(dispatcher) {
        val departments = _uiState.value.departments
        val index = _uiState.value.departments.indexOfFirst { it.isSelected }
        showEmployees.clear()
        val word = value.lowercase()
        employees.forEach { employee ->
            val title = departments[index].title.lowercase()
            if (word.isEmpty() && title in employee.department) showEmployees.add(employee)
            else if (word.isEmpty() && index == 0) showEmployees.add(employee)
            else if (word in employee.firstName.lowercase() || word in employee.lastName.lowercase() || word in employee.userTag.lowercase()) {
                if (index == 0) showEmployees.add(employee)
                else if (title in employee.department) showEmployees.add(employee)
            }
        }
        _uiState.value.filter?.let {
            showEmployees = when (it) {
                FilterMode.ALPHABET -> showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()
                FilterMode.DATE_OF_BIRTH -> showEmployees.sortedBy { it.birthday }.toMutableList()
            }
        }
        _uiState.update {
            it.copy(
                employees = showEmployees.toList(),
                nothingFound = if (showEmployees.isEmpty()) NothingFound.SEARCH else NothingFound.INIT
            )
        }
    }

    private fun cancel() = viewModelScope.launch(dispatcher) {
        val departments = _uiState.value.departments.toMutableList()
        val index = departments.indexOfFirst { it.isSelected }
        showEmployees.clear()
        if (index == 0) {
            showEmployees = employees.toMutableList()
            _uiState.value = HomeUiState(employees = showEmployees.toList())
        } else {
            val word = departments[index].title.lowercase()
            employees.forEach { employee ->
                if (word in employee.department) showEmployees.add(employee)
            }
            _uiState.value = HomeUiState(employees = showEmployees.toList(), departments = departments.toList())
        }
    }

    private fun dialog(show: Boolean) = viewModelScope.launch(dispatcher) {
        if (!show) cancel()
        else _uiState.update { it.copy(showDialog = true) }
    }

    private fun filter(value: FilterMode?) = viewModelScope.launch(dispatcher) {
        if (value == null) cancel()
        else {
            showEmployees = when (value) {
                FilterMode.ALPHABET -> showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()
                FilterMode.DATE_OF_BIRTH -> showEmployees.sortedBy { it.birthday }.toMutableList()
            }
            _uiState.update {
                it.copy(
                    employees = showEmployees.toList(),
                    showDialog = false,
                    filter = value
                )
            }
        }
    }
}

data class HomeUiState(
    val employees: List<EmployeeUi> = emptyList(),
    val departments: List<CategoryUi> = generateDepartments(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val showRefresh: Boolean = false,
    val showDialog: Boolean = false,
    val filter: FilterMode? = null,
    val nothingFound: NothingFound = NothingFound.INIT,
)

enum class NothingFound {
    INIT,
    SEARCH,
}

enum class FilterMode {
    ALPHABET,
    DATE_OF_BIRTH
}

sealed interface Event {
    data object Fetch : Event
    data class SelectCategory(val index: Int) : Event
    data object Refresh : Event
    data class Input(val value: String) : Event
    data object Cancel : Event
    data class Dialog(val show: Boolean) : Event
    data class Filter(val value: FilterMode?) : Event
}