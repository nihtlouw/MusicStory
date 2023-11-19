package com.dicoding.musicstory.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.musicstory.data.StoryRepository
import com.dicoding.musicstory.data.Result
import com.dicoding.musicstory.response.StoryResponse
import com.dicoding.musicstory.utils.Dummy
import com.dicoding.musicstory.utils.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapViewModel: MapVM
    private var dummyStory = Dummy.generateDummyStory()

    @Before
    fun setUp() {
        mapViewModel = MapVM(storyRepository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<Result<StoryResponse>>()
        expectedResponse.value = Result.Success(dummyStory)
        `when`(storyRepository.getStories()).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getStories().getOrAwaitValue()

        Mockito.verify(storyRepository).getStories()
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Get Story Should Null and Return Error`() {
        dummyStory = Dummy.generateErrorDummyStory()

        val expectedResponse = MutableLiveData<Result<StoryResponse>>()
        expectedResponse.value = Result.Error("error")
        `when`(storyRepository.getStories()).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getStories().getOrAwaitValue()

        Mockito.verify(storyRepository).getStories()
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}