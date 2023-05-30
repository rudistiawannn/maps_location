package com.example.storiesapplication.ui.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storiesapplication.response.LoginResponse
import com.example.storiesapplication.response.Story
import com.example.storiesapplication.response.StoryResponse
import com.example.storiesapplication.service.*
import com.example.storiesapplication.util.AppPreferences
import com.example.storiesapplication.util.SingleEvent
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.storiesapplication.service.getAPIService
import com.example.storiesapplication.ui.activity.MainActivity

class MainViewModel(private val appPreferences: AppPreferences) : ViewModel() {
    private val storiesData: MutableLiveData<List<Story>> = MutableLiveData()

    private val storiesError: MutableLiveData<SingleEvent<String>> = MutableLiveData()
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()


    fun getStoriesData(): LiveData<List<Story>> {
        return storiesData
    }

    fun getStoriesError(): LiveData<SingleEvent<String>> {
        return storiesError
    }

    fun isLoading(): LiveData<Boolean> {
        return isLoading
    }

    fun logout() {
        viewModelScope.launch {
            appPreferences.clearPrefs()
        }
    }

    fun loadStoryLocationData(token: String) {
        isLoading.value = true
        val client = getAPIService().getStoryListLocation(token, 100)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    isLoading.value = false
                    storiesData.postValue(response.body()?.listStory)
                } else {
                    isLoading.value = false
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(MainActivity(), "Fail to Upload Image", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "onFailure Call: ${t.message}")
            }
        })
    }
}