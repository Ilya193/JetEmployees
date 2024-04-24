package ru.ikom.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fetchEmployeesUseCase: FetchEmployeesUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val categories = mutableListOf(
        CategoryUi(0, "Android", true),
        CategoryUi(1, "iOS"),
        CategoryUi(2, "Design"),
        CategoryUi(3, "Management"),
        CategoryUi(4, "QA"),
        CategoryUi(5, "Back-office"),
        CategoryUi(6, "Frontend"),
        CategoryUi(7, "HR"),
        CategoryUi(8, "PR"),
        CategoryUi(9, "Backend"),
        CategoryUi(10, "Support"),
        CategoryUi(11, "Analytics"),
    )
    private val _uiState = MutableStateFlow(HomeUiState(categories = categories.toList()))
    val uiState: StateFlow<HomeUiState> get() = _uiState

    fun fetchEmployees() = viewModelScope.launch(dispatcher) {
        val employees = fetchEmployeesUseCase()
        _uiState.update { it.copy(employees = employees.toList()) }
    }

    fun action(event: Event) = viewModelScope.launch(dispatcher) {
        when (event) {
            is Event.SelectCategory -> selectCategory(event.index)
        }
    }

    private fun selectCategory(index: Int) {
        val item = categories[index]
        if (!item.isSelected) {
            for (i in categories.indices) {
                if (categories[i] == item) categories[i] = categories[i].copy(isSelected = true)
                else categories[i] = categories[i].copy(isSelected = false)
            }
            _uiState.update { it.copy(categories = categories.toList()) }
        }
    }
}

data class HomeUiState(
    val employees: List<EmployeeUi> = emptyList(),
    val categories: List<CategoryUi> = emptyList()
)

data class CategoryUi(
    val id: Int,
    val title: String,
    val isSelected: Boolean = false
)

sealed interface Event {
    data class SelectCategory(val index: Int) : Event
}