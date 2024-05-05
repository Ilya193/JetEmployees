package ru.ikom.jetemployees

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.koin.android.ext.android.get
import ru.ikom.common.JetEmployeesTheme
import ru.ikom.details.DetailsScreen
import ru.ikom.home.domain.EmployeesRepository
import ru.ikom.home.presentation.HomeScreen

class MainActivity : ComponentActivity() {
    private val repository: EmployeesRepository = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            JetEmployeesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val root = RootComponentImpl(defaultComponentContext(), repository)
                    Children(
                        stack = root.childStack,
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding(),
                        animation = stackAnimation(slide())
                    ) {
                        when (val instance = it.instance) {
                            is RootComponent.Child.Home -> HomeScreen(component = instance.component)
                            is RootComponent.Child.Details -> DetailsScreen(component = instance.component)
                        }
                    }
                }
            }
        }
    }
}