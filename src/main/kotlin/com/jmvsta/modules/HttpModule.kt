package com.jmvsta.modules

import com.jmvsta.entities.FormData
import com.jmvsta.routes.apiRoute
import com.jmvsta.server.IServer
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics

fun Application.module(mock: IServer) {

    val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    JvmMemoryMetrics().bindTo(registry)
    JvmGcMetrics().bindTo(registry)
    ProcessorMetrics().bindTo(registry)
    JvmThreadMetrics().bindTo(registry)
    UptimeMetrics().bindTo(registry)

    install(ContentNegotiation) {
        json()
    }

    install(CallLogging) {
        level = Level.ERROR
        filter { true }
    }

    install(MicrometerMetrics) {
        this.registry = registry
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
        get("/metrics") {
            call.respondText(registry.scrape())
        }
        apiRoute(mock)
    }
}