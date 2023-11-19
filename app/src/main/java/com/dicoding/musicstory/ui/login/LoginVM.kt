package com.dicoding.musicstory.ui.login

import androidx.lifecycle.ViewModel
import com.dicoding.musicstory.data.StoryRepository

class LoginVM(private val storyRepository: StoryRepository): ViewModel() {
    fun postLogin(email: String, password: String) = storyRepository.login(email, password)
}