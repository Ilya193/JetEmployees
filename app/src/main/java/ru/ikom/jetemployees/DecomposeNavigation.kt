package ru.ikom.jetemployees

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.subscribe
import kotlinx.serialization.Serializable
import ru.ikom.details.DetailsComponent
import ru.ikom.details.DetailsComponentImpl
import ru.ikom.home.domain.EmployeesRepository
import ru.ikom.home.presentation.HomeComponent
import ru.ikom.home.presentation.HomeComponentImpl

interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
        data class Details(val component: DetailsComponent) : Child()
    }
}

class RootComponentImpl(
    componentContext: ComponentContext,
    private val repository: EmployeesRepository
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.List,
            handleBackButton = true,
            childFactory = ::createChildStack,
        )

    private fun createChildStack(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child =
        when (config) {
            is Config.List -> RootComponent.Child.Home(HomeComponentImpl(repository, componentContext, openDetails = {
                navigation.push(Config.Details(it))
            }))
            is Config.Details -> RootComponent.Child.Details(DetailsComponentImpl(config.data, componentContext, back = {
                navigation.pop()
            }))
        }

    @Serializable
    private sealed class Config {
        @Serializable
        data object List : Config()

        @Serializable
        data class Details(val data: String) : Config()
    }
}