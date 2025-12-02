package com.jmvsta.mocks.modules

import com.jmvsta.entities.FormData
import com.jmvsta.mocks.MockServer
import com.jmvsta.mocks.routes.apiRoute
import com.jmvsta.mocks.routes.socketRoute
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

object CallTracker {
    private val calls = mutableListOf<TrackedCall>()

    data class TrackedCall(
        val method: HttpMethod,
        val uri: String,
        val queryParameters: Map<String, String>,
        val body: String?
    )

    fun addCall(call: TrackedCall) {
        calls.add(call)
    }

    fun getCalls(): List<TrackedCall> = calls.toList()

    fun clearCalls() {
        calls.clear()
    }
}

fun Application.module(mock: MockServer = MockServer()) {

    install(ContentNegotiation) {
        json()
    }

    install(CallLogging) {
        level = Level.INFO
        filter { true }
    }

    install(CORS) {
        allowHost("*")
        allowCredentials = true
        maxAgeInSeconds = 1000
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("*")
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }

    install(WebSockets)


    intercept(ApplicationCallPipeline.Monitoring) {
        val method = call.request.httpMethod
        val uri = call.request.uri
        val queryParameters = call.request.queryParameters.toMap().mapValues { it.value.first() }
        val body: String? = if (method in listOf(HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch)) {
            val contentType = call.request.contentType()

            when {
                contentType.match(ContentType.Application.Json) -> {
                    val jsonText = call.receiveText()
                    call.attributes.put(AttributeKey("cachedBody"), jsonText)
                    jsonText
                }
                contentType.match(ContentType.MultiPart.FormData) -> {
                    val multipart = call.receiveMultipart()
                    val formData = mutableMapOf<String, String>()
                    val files = mutableListOf<String>()

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                formData[part.name ?: ""] = part.value
                            }
                            is PartData.FileItem -> {
                                files.add(part.originalFileName ?: "unknown")
                            }
                            else -> {}
                        }
                        part.dispose()
                    }

                    val formBody = Json.encodeToString(FormData(formData, files))
                    call.attributes.put(AttributeKey("cachedBody"), formBody)
                    formBody
                }
                else -> {
                    val channel = call.receiveChannel()
                    val bodyBytes = channel.readRemaining().readByteArray()
                    val bodyText = String(bodyBytes)
                    call.attributes.put(AttributeKey("cachedBody"), bodyText)
                    bodyText
                }
            }
        } else null

        CallTracker.addCall(
            CallTracker.TrackedCall(
                method = method,
                uri = uri,
                queryParameters = queryParameters,
                body = body.toString()
            )
        )
    }

    routing {
        socketRoute(mock)
        apiRoute(mock)
    }
}