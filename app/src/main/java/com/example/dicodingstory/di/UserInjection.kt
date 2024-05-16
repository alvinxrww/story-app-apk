package com.example.dicodingstory.di

import android.content.Context
import com.example.dicodingstory.data.local.UserRepository
import com.example.dicodingstory.data.local.pref.UserPreference
import com.example.dicodingstory.data.local.pref.dataStore

object UserInjection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}