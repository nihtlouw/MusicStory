package com.dicoding.musicstory.ui.stories

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.musicstory.data.StoryRepository
import com.dicoding.musicstory.response.Story

class StoriesVM(repo: StoryRepository): ViewModel() {
    val getListStory: LiveData<PagingData<Story>> =
        repo.getListStories().cachedIn(viewModelScope)
}