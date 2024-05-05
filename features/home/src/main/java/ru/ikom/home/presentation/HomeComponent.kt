package ru.ikom.home.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.ikom.home.domain.EmployeesRepository
import ru.ikom.home.domain.LoadResult

interface HomeComponent {
    val state: StateFlow<Model>

    fun action(event: Event)

    data class Model(
        val employeesState: EmployeesUiState = EmployeesUiState.Loading,
        val departments: List<DepartmentUi> = generateDepartments(),
        val showRefresh: Boolean = false,
        val filterState: FilterUiState = FilterUiState(),
        val input: String = "",
        val loadInformationState: LoadInformation = LoadInformation.INIT,
        val nothingFoundState: NothingFound = NothingFound.INIT
    )
}

class HomeComponentImpl(
    private val repository: EmployeesRepository,
    componentContext: ComponentContext,
    private val openDetails: (String) -> Unit
) : HomeComponent, ComponentContext by componentContext {
    private val scope = componentContext.coroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow(HomeComponent.Model())
    override val state: StateFlow<HomeComponent.Model> = _state.asStateFlow()
    private var employees = mutableListOf<EmployeeUi>()
    private var showEmployees = mutableListOf<EmployeeUi>()

    init {
        fetchEmployees(true)
    }

    override fun action(event: Event) {
        scope.launch {
            when (event) {
                is Event.Fetch -> fetchEmployees()
                is Event.Refresh -> refresh()
                is Event.SelectDepartments -> selectDepartments(event.index)
                is Event.Filter -> filterMode(event.filter, event.show)
                is Event.Input -> input(event.value)
                is Event.Cancel -> input("")
                is Event.OpenDetails -> {
                    withContext(Dispatchers.Main) {
                        openDetails(Json.encodeToString(event.employee))
                    }
                }
            }
        }
    }

    private fun fetchEmployees(init: Boolean = false) = scope.launch() {
        repository.fetchEmployees().collect {
            when (it) {
                is LoadResult.Success -> {
                    employees = it.data.map { it.toEmployeeUi() }.toMutableList()
                    showEmployees = employees.toMutableList()
                    _state.value = HomeComponent.Model(employeesState = EmployeesUiState.Data(showEmployees.toList()))
                }

                is LoadResult.Loading -> {
                    if (it.data.isEmpty() && init) {
                        _state.update {
                            it.copy(
                                employeesState = EmployeesUiState.Loading,
                                loadInformationState = LoadInformation.INIT
                            )
                        }
                    } else if (init) {
                        employees = it.data.map { it.toEmployeeUi() }.toMutableList()
                        showEmployees = employees.toMutableList()

                        _state.update {
                            it.copy(
                                employeesState = EmployeesUiState.Data(showEmployees.toList()),
                                loadInformationState = LoadInformation.LOADING
                            )
                        }
                    }
                }

                is LoadResult.Error -> {
                    if (it.data.isEmpty()) {
                        _state.value = HomeComponent.Model(employeesState = EmployeesUiState.Error)
                    }
                    else {
                        employees = it.data.map { it.toEmployeeUi() }.toMutableList()
                        showEmployees = employees.toMutableList()

                        _state.value = HomeComponent.Model(
                            employeesState = EmployeesUiState.Data(employees.toList()),
                            loadInformationState = LoadInformation.ERROR
                        )
                    }
                }
            }
        }
    }

    private fun refresh() = scope.launch() {
        _state.update { it.copy(showRefresh = true) }
        fetchEmployees()
    }

    private fun selectDepartments(index: Int) =
        scope.launch {
            val departments = _state.value.departments.toMutableList()
            val item = departments[index]
            showEmployees.clear()

            if (!item.isSelected) {
                if (index == 0) {
                    showEmployees = employees.toMutableList()
                    when (_state.value.filterState.filter) {
                        FilterMode.ALPHABET -> showEmployees =
                            showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()

                        FilterMode.DATE_OF_BIRTH -> showEmployees =
                            showEmployees.sortedBy { it.birthday }.toMutableList()

                        else -> {}
                    }

                    _state.update {
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

                    when (_state.value.filterState.filter) {
                        FilterMode.ALPHABET -> showEmployees =
                            showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()

                        FilterMode.DATE_OF_BIRTH -> showEmployees =
                            showEmployees.sortedBy { it.birthday }.toMutableList()

                        else -> {}
                    }

                    _state.update {
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

    private fun filterMode(filter: FilterMode?, show: Boolean) = scope.launch {
        if (show) _state.update { it.copy(filterState = FilterUiState(filter, true)) }
        else {
            when (filter) {
                FilterMode.ALPHABET -> showEmployees =
                    showEmployees.sortedBy { it.firstName + it.lastName }.toMutableList()

                FilterMode.DATE_OF_BIRTH -> showEmployees =
                    showEmployees.sortedBy { it.birthday }.toMutableList()

                null -> {
                    showEmployees.clear()
                    val departments = _state.value.departments.toMutableList()
                    val index = departments.indexOfFirst { it.isSelected }
                    val word = departments[index].name.lowercase()
                    employees.forEach { employee ->
                        if (word in employee.department) showEmployees.add(employee)
                        else if (index == 0) showEmployees.add(employee)
                    }
                }
            }
            val input = _state.value.input.lowercase()
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

            _state.update {
                it.copy(employeesState = EmployeesUiState.Data(
                    employees = showEmployees.toList(),
                    nothingFound = if (showEmployees.isEmpty()) NothingFound.SEARCH else NothingFound.INIT
                ), filterState = FilterUiState(filter, false))
            }
        }
    }

    private fun input(value: String) = scope.launch {
        _state.update { it.copy(input = value) }
        val departments = _state.value.departments
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
        showEmployees = when (_state.value.filterState.filter) {
            FilterMode.ALPHABET -> filtered.sortedBy { it.firstName + it.lastName }.toMutableList()
            FilterMode.DATE_OF_BIRTH -> filtered.sortedBy { it.birthday }.toMutableList()
            else -> filtered
        }
        _state.update {
            it.copy(employeesState = EmployeesUiState.Data(
                employees = showEmployees.toList(),
                nothingFound = if (showEmployees.isEmpty()) NothingFound.SEARCH else NothingFound.INIT
            ))
        }
    }
}