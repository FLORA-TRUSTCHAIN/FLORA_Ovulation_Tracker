package com.example.flora1

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal suspend fun <T> Flow<T>.testFirst(
): T? = coroutineScope {
    var result: T? = null

    val job = launch {
        collect { result = it }
    }
    delay(1L)
    job.cancel()
    return@coroutineScope result
}


internal suspend fun <T> Flow<T>.testToList(
): List<T> = coroutineScope {
    val result = mutableListOf<T>()

    val job = launch {
       this@testToList.collect(result::add)
    }
    delay(1L)
    job.cancel()
    return@coroutineScope result
}
