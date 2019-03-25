package com.github.mduesterhoeft.router.proto

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.github.mduesterhoeft.router.DeserializationHandler
import com.github.mduesterhoeft.router.contentType
import com.google.common.net.MediaType
import com.google.protobuf.GeneratedMessageV3
import java.util.Base64
import kotlin.reflect.KClass
import kotlin.reflect.full.staticFunctions

class ProtoDeserializationHandler: DeserializationHandler {
    private val proto = MediaType.parse("application/x-protobuf")

    override fun supports(input: APIGatewayProxyRequestEvent): Boolean =
        MediaType.parse(input.contentType()).`is`(proto)

    override fun deserialize(input: APIGatewayProxyRequestEvent, target: KClass<*>): Any {
        val bytes = Base64.getDecoder().decode(input.body)
        target.staticFunctions.first { it.name == "parseFrom" && it.parameters.firstOrNull()?.type == bytes. }
    }

}