package ru.ikom.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private var employees = mutableListOf<EmployeeUi>()
    private var showEmployees = mutableListOf<EmployeeUi>()
    private val _employeesUiState = MutableStateFlow<EmployeesUiState>(EmployeesUiState.Loading)
    val employeesUiState: StateFlow<EmployeesUiState> get() = _employeesUiState

    private val _departmentsUiState = MutableStateFlow(DepartmentsUiState())
    val departmentsUiState: StateFlow<DepartmentsUiState> get() = _departmentsUiState

    private val _refreshUiState = MutableStateFlow(RefreshUiState())
    val refreshUiState: StateFlow<RefreshUiState> get() = _refreshUiState

    private val _filterUiState = MutableStateFlow(FilterUiState())
    val filterUiState: StateFlow<FilterUiState> get() = _filterUiState

    private val _inputUiState = MutableStateFlow(InputUiState())
    val inputUiState: StateFlow<InputUiState> get() = _inputUiState

    private val _dataLoadInformationState = MutableStateFlow(DataLoadInformationState())
    val dataLoadInformation: StateFlow<DataLoadInformationState> get() = _dataLoadInformationState

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
                    _employeesUiState.value = EmployeesUiState.Data(showEmployees.toList())
                    _refreshUiState.value = RefreshUiState()
                    _departmentsUiState.value = DepartmentsUiState()
                    _filterUiState.value = FilterUiState()
                    _inputUiState.value = InputUiState()
                    _dataLoadInformationState.value = DataLoadInformationState()
                }
                is LoadResult.Loading -> {
                    if (it.data.isEmpty() && init) {
                        _employeesUiState.value = EmployeesUiState.Loading
                        _dataLoadInformationState.value = DataLoadInformationState()
                    }
                    else if (init) {
                        employees = it.data.map { it.toEmployeeUi() }.toMutableList()
                        showEmployees = employees.toMutableList()
                        _employeesUiState.value = EmployeesUiState.Data(showEmployees.toList())
                        _dataLoadInformationState.value = DataLoadInformationState(state = LoadInformation.LOADING)
                    }
                }
                is LoadResult.Error -> {
                    if (it.data.isEmpty()) _employeesUiState.value = EmployeesUiState.Error
                    else {
                        employees = it.data.map { it.toEmployeeUi() }.toMutableList()
                        showEmployees = employees.toMutableList()
                        _employeesUiState.value = EmployeesUiState.Data(showEmployees.toList())
                        _refreshUiState.value = RefreshUiState()
                        _departmentsUiState.value = DepartmentsUiState()
                        _filterUiState.value = FilterUiState()
                        _inputUiState.value = InputUiState()
                        _dataLoadInformationState.value = DataLoadInformationState(state = LoadInformation.ERROR)
                    }
                }
            }
        }
    }

    private fun refresh() = viewModelScope.launch(dispatcher) {
        _refreshUiState.value = RefreshUiState(true)
        fetchEmployees()
    }

    private fun selectDepartments(index: Int) =
        viewModelScope.launch(dispatcher) {
            val departments = _departmentsUiState.value.departments.toMutableList()
            val item = departments[index]
            showEmployees.clear()

            if (!item.isSelected) {
                if (index == 0) {
                    showEmployees = employees.toMutableList()
                    when (_filterUiState.value.filter) {
                        FilterMode.ALPHABET -> showEmployees = showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()
                        FilterMode.DATE_OF_BIRTH -> showEmployees = showEmployees.sortedBy { it.birthday }.toMutableList()
                        else -> {}
                    }
                    _departmentsUiState.value = DepartmentsUiState()
                    _employeesUiState.value = EmployeesUiState.Data(employees = showEmployees.toList())
                } else {
                    for (i in departments.indices) {
                        if (departments[i] == item) departments[i] = departments[i].copy(isSelected = true)
                        else departments[i] = departments[i].copy(isSelected = false)
                    }

                    val word = item.name.lowercase()
                    employees.forEach { employee ->
                        if (word in employee.department) showEmployees.add(employee)
                    }

                    when (_filterUiState.value.filter) {
                        FilterMode.ALPHABET -> showEmployees = showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()
                        FilterMode.DATE_OF_BIRTH -> showEmployees = showEmployees.sortedBy { it.birthday }.toMutableList()
                        else -> {}
                    }

                    _departmentsUiState.value = DepartmentsUiState(departments = departments)
                    _employeesUiState.value = EmployeesUiState.Data(
                        employees = showEmployees.toList(),
                        nothingFound = if (showEmployees.isEmpty()) NothingFound.SEARCH else NothingFound.INIT
                    )
                    _inputUiState.value = InputUiState()
                }
            }
        }

    private fun filterMode(filter: FilterMode?, show: Boolean) = viewModelScope.launch(dispatcher) {
        if (show) _filterUiState.value = FilterUiState(filter, true)
        else {
            when (filter) {
                FilterMode.ALPHABET -> showEmployees = showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()
                FilterMode.DATE_OF_BIRTH -> showEmployees = showEmployees.sortedBy { it.birthday }.toMutableList()
                null -> {
                    showEmployees.clear()
                    val departments = _departmentsUiState.value.departments.toMutableList()
                    val index = departments.indexOfFirst { it.isSelected }
                    val word = departments[index].name.lowercase()
                    employees.forEach { employee ->
                        if (word in employee.department) showEmployees.add(employee)
                        else if (index == 0) showEmployees.add(employee)
                    }
                }
            }
            val input = _inputUiState.value.input.lowercase()
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
            _filterUiState.value = FilterUiState(filter, false)
            _employeesUiState.value = EmployeesUiState.Data(
                employees = showEmployees.toList(),
                nothingFound = if (showEmployees.isEmpty()) NothingFound.SEARCH else NothingFound.INIT
            )
        }
    }

    private fun input(value: String) = viewModelScope.launch(dispatcher) {
        _inputUiState.value = InputUiState(value)
        val departments = _departmentsUiState.value.departments
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
        showEmployees = when (_filterUiState.value.filter) {
            FilterMode.ALPHABET -> filtered.sortedBy { it.firstName + it.lastName }.toMutableList()
            FilterMode.DATE_OF_BIRTH -> filtered.sortedBy { it.birthday }.toMutableList()
            else -> filtered
        }
        _employeesUiState.value = EmployeesUiState.Data(
            employees = showEmployees.toList(),
            nothingFound = if (showEmployees.isEmpty()) NothingFound.SEARCH else NothingFound.INIT
        )
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