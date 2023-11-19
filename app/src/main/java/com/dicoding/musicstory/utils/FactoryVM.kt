package com.dicoding.musicstory.utils

import com.dicoding.musicstory.data.StoryRepository
import com.dicoding.musicstory.ui.stories.StoriesVM
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.musicstory.inject.Injection
import com.dicoding.musicstory.ui.createstory.CreateStoryVM
import com.dicoding.musicstory.ui.login.LoginVM
import com.dicoding.musicstory.ui.maps.MapVM
import com.dicoding.musicstory.ui.register.RegisterVM

class FactoryVM private constructor(private val repo: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoriesVM::class.java)) {
            return StoriesVM(repo) as T
        }
        if (modelClass.isAssignableFrom(LoginVM::class.java)) {
            return LoginVM(repo) as T
        }
        if (modelClass.isAssignableFrom(RegisterVM::class.java)) {
            return RegisterVM(repo) as T
        }
        if (modelClass.isAssignableFrom(CreateStoryVM::class.java)) {
            return CreateStoryVM(repo) as T
        }
        if (modelClass.isAssignableFrom(MapVM::class.java)) {
            return MapVM(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: FactoryVM? = null
        fun getInstance(context: Context): FactoryVM {
            return instance ?: synchronized(this) {
                instance ?: FactoryVM(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }
}