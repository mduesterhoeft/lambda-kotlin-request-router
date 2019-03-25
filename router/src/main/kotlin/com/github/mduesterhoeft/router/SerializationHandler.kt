package com.github.mduesterhoeft.router

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.MediaType

interface SerializationHandler {

    fun supports(acceptHeader: MediaType, response: ResponseEntity<*>): Boolean

    fun serialize(acceptHeader: MediaType, response: ResponseEntity<*>): String
}

class JsonSerializationHandler(val objectMapper: ObjectMapper): SerializationHandler {

    private val json = MediaType.parse("application/json")

    override fun supports(acceptHeader: MediaType, response: ResponseEntity<*>): Boolean = acceptHeader.`is`(json)

    override fun serialize(acceptHeader: MediaType, response: ResponseEntity<*>): String =
        objectMapper.writeValueAsString(response.body)
}