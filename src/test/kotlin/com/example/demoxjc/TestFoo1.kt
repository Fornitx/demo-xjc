package com.example.demoxjc

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureWebTestClient
class TestFoo1 : AbstractFooTest("/foo1")
