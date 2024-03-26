package com.example.demoxjc

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Component
class RestClient1 {
    private val restTemplate = RestTemplate()

    fun call(): Any? = restTemplate.getForObject<Map<String, Any>>("https://reqres.in/api/users/1")["data"]
}

@Component
class RestClient2 {
    private val restTemplate = RestTemplate()

    fun call(): Any? = restTemplate.getForObject<Map<String, Any>>("https://reqres.in/api/users/2")["data"]
}
