package com.dicoding.musicstory.ui.stories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.musicstory.data.StoryRepository
import com.dicoding.musicstory.response.Story
import com.dicoding.musicstory.utils.Dummy
import com.dicoding.musicstory.utils.getOrAwaitValue
import com.dicoding.musicstory.utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StoriesViewModelTest {
    // Aturan-aturan pengujian
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    // Pengganti dependensi
    @Mock
    private lateinit var storyRepository: StoryRepository
    private var dummyStory = Dummy.generateDummyStory()

    // Pengujian ketika berhasil memuat data cerita
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Story Should Not Null and Return Success`() = mainDispatcherRules.runBlockingTest {
        val data: PagingData<Story> = PagingData.from(dummyStory.listStory)
        val expectedResponse = MutableLiveData<PagingData<Story>>()
        expectedResponse.value = data
        `when`(storyRepository.getListStories()).thenReturn(expectedResponse)

        val storiesViewModel = StoriesVM(storyRepository)
        val actualStory: PagingData<Story> = storiesViewModel.getListStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot()) // Memastikan data tidak null
        Assert.assertEquals(dummyStory.listStory, differ.snapshot()) // Memastikan jumlah data sesuai dengan yang diharapkan
        Assert.assertEquals(dummyStory.listStory.size, differ.snapshot().size) // Memastikan data pertama yang dikembalikan sesuai
        Assert.assertEquals(dummyStory.listStory[0], differ.snapshot()[0]) // Memastikan data pertama yang dikembalikan sesuai
    }

    // Pengujian ketika tidak ada data cerita
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Empty Story Should Return Zero Data`() = mainDispatcherRules.runBlockingTest {
        val emptyList: List<Story> = emptyList()
        val data: PagingData<Story> = PagingData.from(emptyList)
        val expectedResponse = MutableLiveData<PagingData<Story>>()
        expectedResponse.value = data
        `when`(storyRepository.getListStories()).thenReturn(expectedResponse)

        val storiesViewModel = StoriesVM(storyRepository)
        val actualStory: PagingData<Story> = storiesViewModel.getListStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertEquals(0, differ.snapshot().size) // Memastikan jumlah data yang dikembalikan nol
    }


    // Pengganti callback untuk pembaruan daftar
    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
