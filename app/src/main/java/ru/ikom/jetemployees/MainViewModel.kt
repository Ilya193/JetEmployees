package ru.ikom.jetemployees

import androidx.lifecycle.ViewModel

class MainViewModel(
    private val navigation: Navigation<NavigationCommand>
) : ViewModel() {

    fun read() = navigation.read()
    fun coup() = navigation.coup()
}