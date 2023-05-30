package com.example.storiesapplication

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.storiesapplication.response.Story
import com.example.storiesapplication.service.APIService

class StoryRepository (
    private val storyDatabase: StoryDatabase,
    private val apiService: APIService,
    private val token: String
    ) {
        fun getStories(): LiveData<PagingData<Story>> {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(pageSize = 5),
                remoteMediator = StoryRemoteMediator(storyDatabase, apiService,token),
                pagingSourceFactory = { storyDatabase.storyDao().getAllStory() }
            ).liveData
        }
}