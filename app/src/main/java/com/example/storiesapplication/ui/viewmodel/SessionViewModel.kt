package com.example.storiesapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storiesapplication.util.AppPreferences
import kotlinx.coroutines.launch

class SessionViewModel (private val pref: AppPreferences): ViewModel() {
    fun getUserPreferences(): LiveData<String> {
        return pref.getTokenPrefs().asLiveData()
    }

    fun setUserPreferences(token: String) {
        viewModelScope.launch {
            pref.saveTokenPrefs(token)
        }
    }

    fun clearUserPreferences() {
        viewModelScope.launch {
            pref.clearPrefs()
        }
    }
}