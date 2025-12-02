package com.jmvsta.mocks.routes

import com.jmvsta.mocks.MockServer
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.forEach

fun Route.apiRoute(mock: MockServer) {

    route("/api") {
        usersRoute()
        settingsRoute(mock)
        chatsRoute()
        serversRoute(mock)
        messagesRoute(mock)
        downloadsRoute()

        route("/static/{path...}") {
            get {
                val path = call.parameters.getAll("path")?.joinToString("/") ?: ""
                call.respond(HttpStatusCode.OK, "Static path: $path")
            }
        }
    }
}