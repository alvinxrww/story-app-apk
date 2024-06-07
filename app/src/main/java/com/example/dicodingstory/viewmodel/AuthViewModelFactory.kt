package com.example.dicodingstory.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstory.data.user.UserRepository
import com.example.dicodingstory.di.UserInjection

class AuthViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): AuthViewModelFactory {
            if (INSTANCE == null) {
                synchronized(AuthViewModelFactory::class.java) {
                    INSTANCE = AuthViewModelFactory(UserInjection.provideRepository(context))
                }
            }
            return INSTANCE as AuthViewModelFactory
        }
    }
}