package com.jero3000.appinstaller.utils

import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withTimeout
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T> runSecure(
    context: CoroutineContext = EmptyCoroutineContext,
    timeMillis: Long,
    block: () -> T
) = runCatching {
    withTimeout(timeMillis) {
        runStrictInterruptible(context) {
            block()
        }
    }
}.getOrElse { Result.failure(it) }



suspend fun <T> runStrictInterruptible(
    context: CoroutineContext = EmptyCoroutineContext,
    block: () -> T
): Result<T> {
    return runInterruptible(context) {
        var result : Result<T> = Result.failure(Exception("Unknown exception"))
        val thread = thread(name = "runStrictInterruptible") {
            result = runCatching { block() }
        }
        try {
            thread.join()
        }catch (i: InterruptedException){
            thread.interrupt()
            throw i
        }
        result
    }
}