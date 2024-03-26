package com.example.demoxjc

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.time.withTimeout
import org.springframework.stereotype.Service
import java.time.Duration

private val log = KotlinLogging.logger { }

@Service
class DemoService(
    private val restClient1: RestClient1,
    private val restClient2: RestClient2,
) {

    suspend fun foo1(): String {
        return withTimeout(Duration.ofMillis(500)) {
            runInterruptible(Dispatchers.IO) {
                log.info { "<<< call 1" }
                val res1 = restClient1.call()
                log.info { ">>> call 1, result = $res1" }
                log.info { "<<< call 2 " }
                val res2 = restClient2.call()
                log.info { ">>> call 2, result = $res2" }
                res1.toString() + System.lineSeparator() + res2.toString()
            }
        }
    }

    suspend fun foo2(): String {
        return withTimeout(Duration.ofMillis(500)) {
            foo2Inner()
        }
    }

    private suspend fun foo2Inner(): String {
        return supervisorScope {
            val res1Deferred = runAsyncInterruptible {
                log.info { "<<< call 1" }
                val res = restClient1.call()
                log.info { ">>> call 1, result = $res" }
                res?.toString()
            }
            val res2Deferred = runAsyncInterruptible {
                log.info { "<<< call 2" }
                val res = restClient2.call()
                log.info { ">>> call 2, result = $res" }
                res?.toString()
            }
            val res1 = res1Deferred.await()
            if (!res1.isNullOrBlank()) {
                res2Deferred.cancel()
                return@supervisorScope res1
            }
            val res2 = res2Deferred.await()
            return@supervisorScope res2!!
        }
    }

    private fun <T> CoroutineScope.runAsyncInterruptible(block: () -> T) = async {
        runInterruptible(Dispatchers.IO, block)
    }
}
