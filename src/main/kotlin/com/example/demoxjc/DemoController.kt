package com.example.demoxjc

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.coroutines.withLoggingContextAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.time.withTimeout
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.Duration
import java.util.*

private val log = KotlinLogging.logger { }

@RestController
class DemoController(private val service: DemoService) {
    @GetMapping("/foo1")
    suspend fun foo1(@RequestHeader("X-Request-ID") requestId: String): String {
        return withLoggingContextAsync("rqId" to requestId) {
            log.info { ">>> /foo1" }
            service.foo1()
        }
    }

    @GetMapping("/foo2")
    suspend fun foo2(@RequestHeader("X-Request-ID") requestId: String): String {
        return withLoggingContextAsync("rqId" to requestId) {
            log.info { ">>> /foo2" }
            service.foo2()
        }
    }
}
