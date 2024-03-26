package com.example.demoxjc

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

private val log = KotlinLogging.logger { }

@SpringBootTest
@AutoConfigureWebTestClient
class TestFoo1 {
    @Autowired
    private lateinit var client: WebTestClient

    @Test
    fun test() {
        val body = client.get()
            .uri("/foo1")
            .header("X-Request-ID", "123")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList<String>()
            .returnResult()
            .responseBody
        log.info { "body = $body" }
    }
}
