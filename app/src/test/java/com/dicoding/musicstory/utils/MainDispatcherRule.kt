package com.dicoding.musicstory.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(dispatcher) {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Caught unhandled exception: $throwable")
    }

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) {
        dispatcher.runBlockingTest {
            this.block()
        }
    }
}