package com.dicoding.musicstory.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.musicstory.data.StoryRepository
import com.dicoding.musicstory.response.LoginResponse
import com.dicoding.musicstory.utils.Dummy
import com.dicoding.musicstory.utils.getOrAwaitValue
import com.dicoding.musicstory.data.Result
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
class LoginViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginVM
    private var dummyLoginResponse = Dummy.generateDummyLoginSuccess()
    private val dummyEmail = "email"
    private val dummyPassword = "password"

    @Before
    fun setUp() {
        loginViewModel = LoginVM(storyRepository)
    }

    @Test
    fun `when Post Login Should Not Null and Return Success`() {
        val expectedLogin = MutableLiveData<Result<LoginResponse>>()
        expectedLogin.value = Result.Success(dummyLoginResponse)
        `when`(storyRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedLogin)

        val actualResponse = loginViewModel.postLogin(dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(storyRepository).login(dummyEmail, dummyPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Post Login Should Null and Return Error`() {
        dummyLoginResponse = Dummy.generateDummyLoginError()

        val expectedLogin = MutableLiveData<Result<LoginResponse>>()
        expectedLogin.value = Result.Error("invalid password")
        `when`(storyRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedLogin)

        val actualResponse = loginViewModel.postLogin(dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(storyRepository).login(dummyEmail, dummyPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}