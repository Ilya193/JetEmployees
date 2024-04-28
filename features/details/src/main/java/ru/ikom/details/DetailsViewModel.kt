package ru.ikom.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class DetailsViewModel(
    private val router: DetailsRouter,
    private val data: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> get() =  _uiState

    init {
        decode()
    }

    fun decode() = viewModelScope.launch(dispatcher) {
        try {
            val employee = Json.decodeFromString<EmployeeUi>(data)
            _uiState.value = DetailsUiState(employee)
        } catch (_: Exception) {
            _uiState.value = DetailsUiState(error = true)
        }
    }

    fun pop() = router.pop()
}

data class DetailsUiState(val employeeUi: EmployeeUi? = null, val error: Boolean = false)