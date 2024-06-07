package com.example.dicodingstory.di

import android.content.Context
import com.example.dicodingstory.data.remote.api.ApiConfig
import com.example.dicodingstory.data.remote.database.StoryDatabase
import com.example.dicodingstory.data.remote.paging.StoryRepository

object Injection {
    fun provideRepository(context: Context, token: String): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository(database, apiService)
    }
}