package com.jmvsta.mocks.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.downloadsRoute() {
    route("/downloads") {
        post("/start/") {
            val msgId = call.request.queryParameters["msg_id"]
            call.respond(HttpStatusCode.OK, "File download started for msg_id=$msgId")
        }
        post("/stop/") {
            val msgId = call.request.queryParameters["msg_id"]
            call.respond(HttpStatusCode.OK, "File download stopped for msg_id=$msgId")
        }
        post("/status/") {
            val msgId = call.request.queryParameters["msg_id"]
            call.respond(HttpStatusCode.OK, """
                {
                 "msg_id": -1,
                 "user_id": -1,
                 "file_id": "string",
                 "path": "string",
                 "progress": -1,
                 "size": "string",
                 "canceled": true
                }
            """.trimIndent())
        }
        get("/") {
            call.respond(HttpStatusCode.OK, "Test download endpoint")
        }
    }
}