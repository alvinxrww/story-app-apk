//package com.example.dicodingstory.di
//
//import android.content.Context
//import com.example.dicodingstory.data.local.pref.UserPreference
//import com.example.dicodingstory.data.local.pref.dataStore
//import com.example.dicodingstory.data.remote.api.ApiConfig
//import kotlinx.coroutines.runBlocking
//
//object StoryInjection {
//    fun provideRepository(context: Context): StoryRepository {
//        val pref = UserPreference.getInstance(context.dataStore)
//        val user = runBlocking { pref.getSession() }
//        val apiService = ApiConfig.getApiService(user.token)
//        return StoryRepository.getInstance(apiService, pref)
//    }
//}