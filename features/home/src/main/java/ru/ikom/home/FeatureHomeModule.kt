package ru.ikom.home

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.ikom.employees.EmployeesCloudDataSource
import ru.ikom.employees.EmployeesCloudDataSourceImpl

val featureHomeModule = module {
    factory<EmployeesCloudDataSource> {
        EmployeesCloudDataSourceImpl(get())
    }

    factory<FetchEmployeesUseCase> {
        FetchEmployeesUseCase(get())
    }

    viewModel<HomeViewModel> {
        HomeViewModel(get())
    }
}