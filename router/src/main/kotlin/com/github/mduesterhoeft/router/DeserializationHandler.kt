package com.github.mduesterhoeft.router

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.MediaType
import kotlin.reflect.KClass

interface DeserializationHandler {

    fun supports(input: APIGatewayProxyRequestEvent): Boolean

    fun deserialize(input: APIGatewayProxyRequestEvent, target: KClass<*>): Any
}

class JsonDeserializationHandler(val objectMapper: ObjectMapper): DeserializationHandler {

    private val json = MediaType.parse("application/json")

    override fun supports(input: APIGatewayProxyRequestEvent) =
            MediaType.parse(input.contentType()).`is`(json)

    override fun deserialize(input: APIGatewayProxyRequestEvent, target: KClass<*>) =
        when (target) {
            Unit::class -> Unit
            String::class -> input.body!!
            else -> objectMapper.readValue(input.body, target.java)
        }
}

