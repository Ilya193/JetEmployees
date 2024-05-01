package ru.ikom.home.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.ikom.employees.EmployeesCacheDataSource
import ru.ikom.employees.EmployeesCacheDataSourceImpl
import ru.ikom.employees.EmployeesCloudDataSource
import ru.ikom.employees.EmployeesCloudDataSourceImpl
import ru.ikom.home.data.EmployeesRepositoryImpl
import ru.ikom.home.domain.EmployeesRepository
import ru.ikom.home.presentation.HomeViewModel

val featureHomeModule = module {
    factory<EmployeesCloudDataSource> {
        EmployeesCloudDataSourceImpl(get())
    }

    factory<EmployeesCacheDataSource> {
        EmployeesCacheDataSourceImpl(get())
    }

    factory<EmployeesRepository> {
        EmployeesRepositoryImpl(get(), get())
    }

    viewModel<HomeViewModel> {
        HomeViewModel(get(), get())
    }
}