package ru.ikom.jetemployees

import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.ikom.details.DetailsRouter
import ru.ikom.home.presentation.HomeRouter

interface Navigation<T> {
    fun read(): StateFlow<T>
    fun update(value: T)
    fun coup()

    class Base : Navigation<Screen>, HomeRouter, DetailsRouter {
        private val screen = MutableStateFlow<Screen>(Screen.Start)

        override fun read(): StateFlow<Screen> = screen.asStateFlow()

        override fun update(value: Screen) {
            screen.value = value
        }

        override fun coup() {
            update(Screen.Coup)
        }

        override fun openDetails(data: String) {
            update(DetailsScreen(data))
        }

        override fun pop() {
            update(Screen.Pop())
        }
    }
}

interface Screen {
    fun show(navController: NavController) = Unit

    abstract class ReplaceWithArguments(
        private val route: String,
        private val data: String,
    ) : Screen {
        override fun show(navController: NavController) {
            navController.navigate(
                route.replace(
                    "{data}",
                    data
                )
            )
        }
    }

    data object Start : Screen
    data object Coup : Screen

    class Pop : Screen {
        override fun show(navController: NavController) {
            navController.popBackStack()
        }
    }
}

class DetailsScreen(
    data: String,
) : Screen.ReplaceWithArguments(Screens.DETAILS, data)