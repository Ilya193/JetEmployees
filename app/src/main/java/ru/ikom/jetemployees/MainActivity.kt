package ru.ikom.jetemployees

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import ru.ikom.common.JetEmployeesTheme
import ru.ikom.details.DetailsScreen
import ru.ikom.home.presentation.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            JetEmployeesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
    }
}

@Composable
fun Content(viewModel: MainViewModel = koinViewModel()) {
    val navController = rememberNavController()
    val screen by viewModel.read().collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.coup()
        }
    }

    LaunchedEffect(screen) {
        screen.show(navController)
    }

    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        navController = navController,
        startDestination = Screens.HOME
    ) {
        composable(Screens.HOME) {
            HomeScreen()
        }

        composable(Screens.DETAILS) {
            DetailsScreen(it.arguments?.getString("data") ?: "")
        }
    }

}