package com.example.storiesapplication.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storiesapplication.ui.viewmodel.*

class ViewModelFactory(private val appPreferences: AppPreferences) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateStoryViewModel::class.java)) {
            return CreateStoryViewModel(appPreferences) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(appPreferences) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(appPreferences) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(appPreferences) as T
        } else if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            return SessionViewModel(appPreferences) as T
        }
        throw IllegalArgumentException(modelClass.name + " not found")
    }
}