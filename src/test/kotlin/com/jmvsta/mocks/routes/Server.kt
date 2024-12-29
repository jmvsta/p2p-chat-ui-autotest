package com.jmvsta.mocks.routes

import com.jmvsta.entities.MessageDto
import com.jmvsta.entities.Server
import com.jmvsta.mocks.MockServer
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.serversRoute(mock: MockServer) {
    route("/servers") {
        post("/") {
            val jsonString = call.receiveText()
            mock.servers.add(Server.create("http://localhost:8080", "active"))
            call.respond(HttpStatusCode.OK, "Ok")
        }
        get("/list/") {
            call.respond(
                HttpStatusCode.OK, Json.encodeToString(mock.servers).trimIndent()
            )
        }
        delete {
            call.respond(HttpStatusCode.OK, "Server deleted")
        }
    }
}