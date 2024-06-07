package com.example.dicodingstory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.story.api.ApiConfig
import com.example.dicodingstory.data.story.response.ListStoryItem
import com.example.dicodingstory.data.story.response.RegisterResponse
import com.example.dicodingstory.data.story.response.StoryResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryViewModel : ViewModel() {

    private val _storiesLocations = MutableLiveData<List<ListStoryItem>>()
    val storiesLocations: LiveData<List<ListStoryItem>> = _storiesLocations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getStoriesWithLocation(token: String?) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getStoriesWithLocation()
                _storiesLocations.value = response.listStory

                _isLoading.value = false
            } catch (e: HttpException) {
                _isLoading.value = false

                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, StoryResponse::class.java)
                val errorMessage = errorBody.message
                _errorMessage.value = errorMessage.toString()
            }
        }
    }

    fun postStory(
        token: String?,
        multipartBody: Part,
        requestBody: RequestBody
    ) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(token)
                val successResponse = apiService.uploadImage(multipartBody, requestBody)
                _successMessage.value = successResponse.message
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                _errorMessage.value = errorResponse.message ?: "Network Error"
            }

            _isLoading.value = false
        }
    }
}