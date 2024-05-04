package ru.ikom.details

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureDetailsModule = module {
    viewModel<DetailsViewModel> { params ->
        DetailsViewModel(get(), data = params.get())
    }
}