package com.dicoding.musicstory.ui.register

import androidx.lifecycle.ViewModel
import com.dicoding.musicstory.data.StoryRepository

class RegisterVM(private val storyRepository: StoryRepository): ViewModel() {
    fun postRegister(name: String, email: String, password: String) = storyRepository.register(name, email, password)
}