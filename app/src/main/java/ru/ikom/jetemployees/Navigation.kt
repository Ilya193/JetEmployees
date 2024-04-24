package ru.ikom.jetemployees

import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface Navigation<T> {
    fun read(): StateFlow<T>
    fun update(value: T)
    fun coup()

    class Base : Navigation<Screen> {
        private val screen = MutableStateFlow<Screen>(Screen.Start)

        override fun read(): StateFlow<Screen> = screen.asStateFlow()

        override fun update(value: Screen) {
            screen.value = value
        }

        override fun coup() {
            update(Screen.Coup)
        }
    }
}

interface Screen {
    fun show(navController: NavController) = Unit

    abstract class ReplaceWithArguments(
        private val route: String,
        private val data: String,
    ) : Screen {
        override fun show(navController: NavController) = navController.navigate(
            route.replace(
                "{data}",
                data
            )
        )
    }

    data object Start : Screen
    data object Coup : Screen

    class Pop : Screen {
        override fun show(navController: NavController) {
            navController.popBackStack()
        }
    }
}