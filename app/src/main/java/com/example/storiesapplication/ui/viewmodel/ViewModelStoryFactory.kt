package com.example.storiesapplication.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storiesapplication.StoryDatabase
import com.example.storiesapplication.StoryRepository
import com.example.storiesapplication.service.APIService

@Suppress("UNCHECKED_CAST")
class ViewModelStoryFactory(val context: Context, private val apiService: APIService, val token: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryPagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val database = StoryDatabase.getDatabase(context)
            return StoryPagerViewModel(
                StoryRepository(
                    database,
                    apiService, token
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}