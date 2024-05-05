package ru.ikom.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

interface DetailsComponent {
    val state: StateFlow<Model>

    fun pop()

    data class Model(
        val employee: EmployeeUi? = null,
        val error: Boolean = false
    )
}

class DetailsComponentImpl(
    private val data: String,
    componentContext: ComponentContext,
    private val back: () -> Unit
) : DetailsComponent, ComponentContext by componentContext {

    private val _state = MutableStateFlow(DetailsComponent.Model())
    override val state: StateFlow<DetailsComponent.Model> = _state.asStateFlow()

    override fun pop() {
        back()
    }

    private val scope = componentContext.coroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            try {
                val employee = Json.decodeFromString<EmployeeUi>(data)
                _state.value = DetailsComponent.Model(employee)
            } catch (_: Exception) {
                _state.value = DetailsComponent.Model(error = true)
            }
        }
    }

}