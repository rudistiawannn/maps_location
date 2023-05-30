package com.example.storiesapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storiesapplication.response.LoginResponse
import com.example.storiesapplication.service.UNEXPECTED_DATA_ERROR
import com.example.storiesapplication.service.UNEXPECTED_ERROR
import com.example.storiesapplication.service.formatResponseCode
import com.example.storiesapplication.service.getAPIService
import com.example.storiesapplication.util.AppPreferences
import com.example.storiesapplication.util.SingleEvent
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val appPreferences: AppPreferences) : ViewModel() {
    private val loginData: MutableLiveData<LoginResponse> = MutableLiveData()
    private val loginError: MutableLiveData<SingleEvent<String>> = MutableLiveData()
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun getLoginData(): LiveData<LoginResponse> {
        return loginData
    }

    fun getLoginError(): LiveData<SingleEvent<String>> {
        return loginError
    }

    fun isLoading(): LiveData<Boolean> {
        return isLoading
    }

    fun login(email: String, password: String) {
        isLoading.value = true

        getAPIService().login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                isLoading.value = false

                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.error as Boolean) {
                            loginError.value = SingleEvent(it.message as String)
                        } else {
                            loginData.value = response.body() as LoginResponse

                            viewModelScope.launch {
                                response.body()?.loginResult?.let { loginData ->
                                    appPreferences.saveTokenPrefs(loginData.token ?: "")
                                }
                            }
                        }
                    } ?: run {
                        loginError.value = SingleEvent(UNEXPECTED_DATA_ERROR)
                    }
                } else {
                    val body: LoginResponse? =
                        Gson().fromJson(response.errorBody()?.string(), LoginResponse::class.java)

                    loginError.value =
                        SingleEvent(body?.message ?: formatResponseCode(response.code()))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                isLoading.value = false
                loginError.value = SingleEvent(UNEXPECTED_ERROR)
            }
        })
    }
}