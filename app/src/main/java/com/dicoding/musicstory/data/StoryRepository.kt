package com.dicoding.musicstory.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.musicstory.api.ApiService
import com.dicoding.musicstory.response.*
import com.dicoding.musicstory.preference.LoginPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository (private val pref: LoginPreference, private val apiService: ApiService) {
    fun getListStories(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(pref, apiService),
            pagingSourceFactory = {
                StoryPagingSource(pref, apiService)
            }
        ).liveData
    }

    fun login(
        email: String,
        password: String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(
                email,
                password
            )
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(
                name,
                email,
                password
            )
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun createStory(
        imageFile: MultipartBody.Part,
        desc: RequestBody,
        lat: Double,
        lon: Double
    ): LiveData<Result<CreateStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.createStory(
                token = "Bearer ${pref.getUser().token}",
                file = imageFile,
                description = desc,
                lat = lat,
                lon = lon
            )
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStories(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStory(
                token = "Bearer ${pref.getUser().token}",
                location = 1
            )
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            preferences: LoginPreference,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(preferences, apiService)
            }.also { instance = it }
    }
}