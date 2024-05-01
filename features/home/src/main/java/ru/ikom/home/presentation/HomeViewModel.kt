package ru.ikom.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.ikom.home.domain.EmployeesRepository
import ru.ikom.home.domain.LoadResult

class HomeViewModel(
    private val router: HomeRouter,
    private val repository: EmployeesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var employees = mutableListOf<EmployeeUi>()
    private var showEmployees = mutableListOf<EmployeeUi>()

    init {
        fetchEmployees(true)
    }

    fun action(event: Event) = viewModelScope.launch(dispatcher) {
        when (event) {
            is Event.Fetch -> fetchEmployees()
            is Event.Refresh -> refresh()
            is Event.SelectDepartments -> selectDepartments(event.index)
            is Event.Filter -> filterMode(event.filter, event.show)
            is Event.Input -> input(event.value)
            is Event.Cancel -> input("")
            is Event.OpenDetails -> router.openDetails(Json.encodeToString(event.employee))
        }
    }

    private fun fetchEmployees(init: Boolean = false) = viewModelScope.launch(dispatcher) {
        repository.fetchEmployees().collect {
            when (it) {
                is LoadResult.Success -> {
                    employees = it.data.map { it.toEmployeeUi() }.toMutableList()
                    showEmployees = employees.toMutableList()
                    _uiState.value = HomeUiState(employeesState = EmployeesUiState.Data(showEmployees.toList()))
                }

                is LoadResult.Loading -> {
                    if (it.data.isEmpty() && init) {
                        _uiState.update {
                            it.copy(
                                employeesState = EmployeesUiState.Loading,
                                loadInformationState = LoadInformation.INIT
                            )
                        }
                    } else if (init) {
                        employees = it.data.map { it.toEmployeeUi() }.toMutableList()
                        showEmployees = employees.toMutableList()

                        _uiState.update {
                            it.copy(
                                employeesState = EmployeesUiState.Data(showEmployees.toList()),
                                loadInformationState = LoadInformation.LOADING
                            )
                        }
                    }
                }

                is LoadResult.Error -> {
                    if (it.data.isEmpty()) {
                        _uiState.value = HomeUiState(employeesState = EmployeesUiState.Error)
                    }
                    else {
                        employees = it.data.map { it.toEmployeeUi() }.toMutableList()
                        showEmployees = employees.toMutableList()

                        _uiState.value = HomeUiState(
                            employeesState = EmployeesUiState.Data(employees.toList()),
                            loadInformationState = LoadInformation.ERROR
                        )
                    }
                }
            }
        }
    }

    private fun refresh() = viewModelScope.launch(dispatcher) {
        _uiState.update { it.copy(showRefresh = true) }
        fetchEmployees()
    }

    private fun selectDepartments(index: Int) =
        viewModelScope.launch(dispatcher) {
            val departments = uiState.value.departments.toMutableList()
            val item = departments[index]
            showEmployees.clear()

            if (!item.isSelected) {
                if (index == 0) {
                    showEmployees = employees.toMutableList()
                    when (uiState.value.filterState.filter) {
                        FilterMode.ALPHABET -> showEmployees =
                            showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()

                        FilterMode.DATE_OF_BIRTH -> showEmployees =
                            showEmployees.sortedBy { it.birthday }.toMutableList()

                        else -> {}
                    }

                    _uiState.update {
                        it.copy(
                            employeesState = EmployeesUiState.Data(showEmployees.toList()),
                            departments = generateDepartments(),
                            input = ""
                        )
                    }
                } else {
                    for (i in departments.indices) {
                        if (departments[i] == item) departments[i] =
                            departments[i].copy(isSelected = true)
                        else departments[i] = departments[i].copy(isSelected = false)
                    }

                    val word = item.name.lowercase()
                    employees.forEach { employee ->
                        if (word in employee.department) showEmployees.add(employee)
                    }

                    when (uiState.value.filterState.filter) {
                        FilterMode.ALPHABET -> showEmployees =
                            showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()

                        FilterMode.DATE_OF_BIRTH -> showEmployees =
                            showEmployees.sortedBy { it.birthday }.toMutableList()

                        else -> {}
                    }

                    _uiState.update {
                        it.copy(
                            employeesState = EmployeesUiState.Data(
                                showEmployees.toList(),
                                nothingFound = if (showEmployees.isEmpty()) NothingFound.SEARCH else NothingFound.INIT
                            ),
                            departments = departments,
                            input = ""
                        )
                    }
                }
            }
        }

    private fun filterMode(filter: FilterMode?, show: Boolean) = viewModelScope.launch(dispatcher) {
        if (show) _uiState.update { it.copy(filterState = FilterUiState(filter, true)) }
        else {
            when (filter) {
                FilterMode.ALPHABET -> showEmployees =
                    showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()

                FilterMode.DATE_OF_BIRTH -> showEmployees =
                    showEmployees.sortedBy { it.birthday }.toMutableList()

                null -> {
                    showEmployees.clear()
                    val departments = uiState.value.departments.toMutableList()
                    val index = departments.indexOfFirst { it.isSelected }
                    val word = departments[index].name.lowercase()
                    employees.forEach { employee ->
                        if (word in employee.department) showEmployees.add(employee)
                        else if (index == 0) showEmployees.add(employee)
                    }
                }
            }
            val input = uiState.value.input.lowercase()
            if (input.isNotEmpty()) {
                val filtered = mutableListOf<EmployeeUi>()
                showEmployees.forEach { employee ->
                    if (input in employee.firstName.lowercase() ||
                        input in employee.lastName.lowercase() ||
                        input in employee.userTag.lowercase()
                    ) filtered.add(employee)
                }
                showEmployees = filtered
            }

            _uiState.update {
                it.copy(employeesState = EmployeesUiState.Data(
                    employees = showEmployees.toList(),
                    nothingFound = if (showEmployees.isEmpty()) NothingFound.SEARCH else NothingFound.INIT
                ), filterState = FilterUiState(filter, false))
            }
        }
    }

    private fun input(value: String) = viewModelScope.launch(dispatcher) {
        _uiState.update { it.copy(input = value) }
        val departments = uiState.value.departments
        val index = departments.indexOfFirst { it.isSelected }
        val word = value.lowercase()
        val filtered = mutableListOf<EmployeeUi>()
        employees.forEach { employee ->
            val departmentName = departments[index].name.lowercase()
            if (word.isEmpty() && departmentName in employee.department) filtered.add(employee)
            else if (word.isEmpty() && index == 0) filtered.add(employee)
            else if (word in employee.firstName.lowercase() || word in employee.lastName.lowercase() || word in employee.userTag.lowercase()) {
                if (index == 0) filtered.add(employee)
                else if (departmentName in employee.department) filtered.add(employee)
            }
        }
        showEmployees = when (uiState.value.filterState.filter) {
            FilterMode.ALPHABET -> filtered.sortedBy { it.firstName + it.lastName }.toMutableList()
            FilterMode.DATE_OF_BIRTH -> filtered.sortedBy { it.birthday }.toMutableList()
            else -> filtered
        }
        _uiState.update {
            it.copy(employeesState = EmployeesUiState.Data(
                employees = showEmployees.toList(),
                nothingFound = if (showEmployees.isEmpty()) NothingFound.SEARCH else NothingFound.INIT
            ))
        }
    }
}

sealed interface Event {
    data object Fetch : Event
    data object Refresh : Event
    data class SelectDepartments(
        val index: Int,
    ) : Event

    data class Filter(
        val filter: FilterMode?,
        val show: Boolean,
    ) : Event

    data class Input(
        val value: String,
    ) : Event

    data object Cancel : Event

    data class OpenDetails(val employee: EmployeeUi) : Event
}