package com.dicoding.musicstory.inject

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.musicstory.api.ApiConfig
import com.dicoding.musicstory.data.StoryRepository
import com.dicoding.musicstory.preference.LoginPreference

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("storiesin")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val preferences = LoginPreference(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(preferences, apiService)
    }
}