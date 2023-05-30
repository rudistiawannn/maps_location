package com.example.storiesapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storiesapplication.StoryRepository
import com.example.storiesapplication.response.Story

class StoryPagerViewModel (storyRepository: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<Story>> =
        storyRepository.getStories().cachedIn(viewModelScope)
}