package ru.ikom.jetemployees

import androidx.room.Room
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.ikom.database.EmployeesDao
import ru.ikom.database.EmployeesDb
import ru.ikom.details.DetailsRouter
import ru.ikom.home.presentation.HomeRouter
import ru.ikom.network.EmployeesService

val appModule = module {
    val navigation = Navigation.Base()

    single<Navigation<Screen>> { navigation }

    single<HomeRouter> { navigation }

    single<DetailsRouter> { navigation }

    viewModel<MainViewModel> {
        MainViewModel(get())
    }

    single<EmployeesService> {
        Retrofit.Builder()
            .baseUrl("https://stoplight.io/")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(EmployeesService::class.java)
    }

    single<EmployeesDao> {
        Room.databaseBuilder(get(), EmployeesDb::class.java, "employees.db").build().employeesDao()
    }
}