package com.example.demoxjc

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureWebTestClient
class TestFoo2 : AbstractFooTest("/foo2")
