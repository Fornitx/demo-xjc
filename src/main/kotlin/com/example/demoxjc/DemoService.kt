package com.example.demoxjc

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.time.withTimeout
import org.springframework.beans.factory.DisposableBean
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger { }

private const val TIMEOUT = 1000L

@Service
class DemoService(
    private val restClient1: RestClient1,
    private val restClient2: RestClient2,
) : DisposableBean {
    private val executor = ThreadPoolTaskExecutor().apply {
        corePoolSize = 8
        maxPoolSize = 255
        queueCapacity = Int.MAX_VALUE
        initialize()
    }

    suspend fun foo1(): String {
        return try {
            withTimeout(Duration.ofMillis(TIMEOUT)) {
                runInterruptible(Dispatchers.IO) {
                    val res1 = call1WithLog()
                    val res2 = call2WithLog()
                    res1.toString() + System.lineSeparator() + res2.toString()
                }
            }
        } catch (ex: TimeoutCancellationException) {
            "TIMEOUT $TIMEOUT"
        }
    }

    suspend fun foo2(): String {
        return try {
            withTimeout(TIMEOUT) {
                val res1Deferred = runAsyncInterruptible {
                    call1WithLog()?.toString()
                }
                val res2Deferred = runAsyncInterruptible {
                    call2WithLog()?.toString()
                }
                val res1 = res1Deferred.await()
                if (!res1.isNullOrBlank()) {
                    res2Deferred.cancelWithLog()
                    return@withTimeout res1
                }
                val res2 = res2Deferred.await()
                return@withTimeout res2!!
            }
        } catch (ex: TimeoutCancellationException) {
            "TIMEOUT $TIMEOUT"
        }
    }

    suspend fun foo3(): String {
        return try {
            withTimeout(Duration.ofMillis(TIMEOUT)) {
                foo3Inner()
            }
        } catch (ex: TimeoutCancellationException) {
            "TIMEOUT $TIMEOUT"
        }
    }

    private suspend fun foo3Inner(): String {
        val (res1Deferred, res2Deferred) = supervisorScope {
            runAsyncInterruptible {
                call1WithLog()?.toString()
            } to runAsyncInterruptible {
                call2WithLog()?.toString()
            }
        }
        val res1 = res1Deferred.await()
        if (!res1.isNullOrBlank()) {
            res2Deferred.cancelWithLog()
            return res1
        }
        val res2 = res2Deferred.await()
        return res2!!
    }

    suspend fun foo4(): String {
        return try {
            withTimeout(Duration.ofMillis(TIMEOUT)) {
                foo4Inner()
            }
        } catch (ex: TimeoutCancellationException) {
            "TIMEOUT $TIMEOUT"
        }
    }

    private suspend fun foo4Inner(): String {
        return supervisorScope {
            val res1Deferred = runAsyncInterruptible {
                call1WithLog()?.toString()
            }
            val res2Deferred = runAsyncInterruptible {
                call2WithLog()?.toString()
            }
            val res1 = res1Deferred.await()
            if (!res1.isNullOrBlank()) {
                res2Deferred.cancelWithLog()
                return@supervisorScope res1
            }
            val res2 = res2Deferred.await()
            return@supervisorScope res2!!
        }
    }

    suspend fun foo5(): String {
        return try {
            withTimeout(Duration.ofMillis(TIMEOUT)) {
                foo5Inner()
            }
        } catch (ex: TimeoutCancellationException) {
            "TIMEOUT $TIMEOUT"
        }
    }

    private suspend fun foo5Inner(): String {
        return supervisorScope {
            val res1Deferred = runAsyncInterruptible {
                val future = CompletableFuture.supplyAsync({
                    call1WithLog()?.toString()
                }, executor)
                future.get(5, TimeUnit.SECONDS)
            }
            val res2Deferred = runAsyncInterruptible {
                val future = CompletableFuture.supplyAsync({
                    call2WithLog()?.toString()
                }, executor)
                try {
                    future.get(5, TimeUnit.SECONDS)
                } catch (ex: Exception) {
                    future.completeExceptionally(ex)
                    log.error(ex) { "Exception in Future" }
                    throw ex
                }
            }
            val res1 = res1Deferred.await()
            if (!res1.isNullOrBlank()) {
                res2Deferred.cancelWithLog()
                return@supervisorScope res1
            }
            val res2 = res2Deferred.await()
            return@supervisorScope res2!!
        }
    }

    private fun call1WithLog(): Any? {
        log.info { "<<< call 1" }
        val res1 = restClient1.call()
        log.info { ">>> call 1, result = $res1" }
        return res1
    }

    private fun call2WithLog(): Any? {
        log.info { "<<< call 2 " }
        val res2 = restClient2.call()
        log.info { ">>> call 2, result = $res2" }
        return res2
    }

    private fun <T> Deferred<T>.cancelWithLog() {
        if (!isCompleted) {
            log.info("Canceling res2Deferred")
            cancel()
            log.info("Canceled res2Deferred")
        }
    }

    private fun <T> CoroutineScope.runAsyncInterruptible(block: () -> T) = async {
        runInterruptible(Dispatchers.IO, block)
    }

    override fun destroy() {
        executor.destroy()
    }
}
