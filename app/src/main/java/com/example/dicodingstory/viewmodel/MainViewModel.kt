package com.example.dicodingstory.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dicodingstory.data.story.paging.StoryRepository
import com.example.dicodingstory.data.story.response.ListStoryItem
import com.example.dicodingstory.di.Injection

class MainViewModel(storyRepository: StoryRepository) : ViewModel() {

    val story: LiveData<PagingData<ListStoryItem>> =
         storyRepository.getStory().cachedIn(viewModelScope)

}

class ViewModelFactory(private val context: Context, val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(Injection.provideRepository(context, token)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}