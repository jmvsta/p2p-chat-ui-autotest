package com.jmvsta.mocks.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.chatsRoute() {
    route("/chats") {
        get("/list/") {
            val offset = call.request.queryParameters["offset"]
            val limit = call.request.queryParameters["limit"]
            val filterBanned = call.request.queryParameters["filter_banned"]
            call.respond(
                HttpStatusCode.OK, """
                            {"chats":[{"id":"U1","name":"test2412018081","last_msg_user":null,"last_msg_txt":"a1","read":true,"last_active":"2024-12-23T17:17:44"}]}
                        """.trimIndent()
            )
        }
    }
}