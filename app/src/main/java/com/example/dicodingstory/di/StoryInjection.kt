package com.example.dicodingstory.di

import android.content.Context
import com.example.dicodingstory.data.story.api.ApiConfig
import com.example.dicodingstory.data.story.database.StoryDatabase
import com.example.dicodingstory.data.story.paging.StoryRepository

object StoryInjection {
    fun provideRepository(context: Context, token: String): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository(database, apiService)
    }
}