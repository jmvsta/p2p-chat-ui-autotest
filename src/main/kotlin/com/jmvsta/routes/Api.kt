package com.jmvsta.routes

import com.jmvsta.server.IServer
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.apiRoute(mock: IServer) {

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