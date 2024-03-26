package com.example.demoxjc

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.internal.stubbing.answers.AnswersWithDelay
import org.mockito.internal.stubbing.answers.CallsRealMethods
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import kotlin.reflect.jvm.jvmName

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
abstract class AbstractFooTest(private val uri: String) {
    protected val log = KotlinLogging.logger(this::class.jvmName)

    @Autowired
    protected lateinit var client: WebTestClient

    @SpyBean
    protected lateinit var restClient1: RestClient1

    @SpyBean
    protected lateinit var restClient2: RestClient2

    @Order(1)
    @Test
    fun allOk() {
        val body = client.get()
            .uri(uri)
            .header("X-Request-ID", "123")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<String>()
            .returnResult()
            .responseBody
        log.info { "body = $body" }
    }

    @Order(2)
    @Test
    fun returnOkWhenSecondThrow() {
        whenever(restClient1.call()).doAnswer(AnswersWithDelay(500, CallsRealMethods()))
        whenever(restClient2.call()).doThrow(RuntimeException("azaza"))

        val body = client.get()
            .uri(uri)
            .header("X-Request-ID", "123")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<String>()
            .returnResult()
            .responseBody
        log.info { "body = $body" }
    }

    @Order(3)
    @Test
    fun cancelSecondWhenSecondDelayed() {
        whenever(restClient2.call()).doAnswer(AnswersWithDelay(500, CallsRealMethods()))

        val body = client.get()
            .uri(uri)
            .header("X-Request-ID", "123")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<String>()
            .returnResult()
            .responseBody
        log.info { "body = $body" }

        Thread.sleep(600)
    }

    @Order(4)
    @Test
    fun throwThenFirstTimeout() {
        whenever(restClient1.call()).doAnswer(AnswersWithDelay(5000, CallsRealMethods()))

        val body = client.get()
            .uri(uri)
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
