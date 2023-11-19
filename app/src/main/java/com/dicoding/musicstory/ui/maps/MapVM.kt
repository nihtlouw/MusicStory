package com.dicoding.musicstory.ui.maps

import androidx.lifecycle.ViewModel
import com.dicoding.musicstory.data.StoryRepository

class MapVM(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories() = storyRepository.getStories()
}