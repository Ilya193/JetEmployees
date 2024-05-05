package ru.ikom.jetemployees

import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.forward
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.ikom.details.DetailsRouter
import ru.ikom.details.DetailsScreen
import ru.ikom.home.presentation.HomeRouter

sealed interface NavigationCommand {
    data object Init : NavigationCommand
    data class Forward(val screen: Screen) : NavigationCommand
    data object Pop : NavigationCommand
}

fun NavigationContainer<StackState>.launchScreen(command: NavigationCommand) {
    when (command) {
        is NavigationCommand.Init -> {}
        is NavigationCommand.Forward -> forward(command.screen)
        is NavigationCommand.Pop -> back()
    }
}

interface Navigation<T> {
    fun read(): StateFlow<T>
    fun update(value: T)
    fun coup()

    class Base : Navigation<NavigationCommand>, HomeRouter, DetailsRouter {
        private val screen = MutableStateFlow<NavigationCommand>(NavigationCommand.Init)

        override fun read(): StateFlow<NavigationCommand> = screen.asStateFlow()

        override fun update(value: NavigationCommand) {
            screen.value = value
        }

        override fun coup() {
            update(NavigationCommand.Init)
        }

        override fun openDetails(data: String) {
            TODO()
            //update(NavigationCommand.Forward(DetailsScreen(data)))
        }

        override fun pop() {
            update(NavigationCommand.Pop)
        }
    }
}