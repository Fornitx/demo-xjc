package com.example.demoxjc

import ch.qos.logback.core.util.TimeUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.mockito.internal.stubbing.answers.AnswersWithDelay
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger { }

@SpringBootTest
@AutoConfigureWebTestClient
class TestFoo2 {
    @Autowired
    private lateinit var client: WebTestClient

    @SpyBean
    private lateinit var restClient2: RestClient2

    @Test
    fun test1() {
        val body = client.get()
            .uri("/foo2")
            .header("X-Request-ID", "123")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<String>()
            .returnResult()
            .responseBody
        log.info { "body = $body" }
    }

    @Test
    fun test2() {
        doThrow(RuntimeException("azaza")).whenever(restClient2).call()

        val body = client.get()
            .uri("/foo2")
            .header("X-Request-ID", "123")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<String>()
            .returnResult()
            .responseBody
        log.info { "body = $body" }
    }

    @Test
    fun test3() {
        doAnswer {
            TimeUnit.SECONDS.sleep(5)
            "MOCKED"
        }.whenever(restClient2).call()

        val body = client.get()
            .uri("/foo2")
            .header("X-Request-ID", "123")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<String>()
            .returnResult()
            .responseBody
        log.info { "body = $body" }
    }
}
