package com.jmvsta.mocks.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.usersRoute() {
    route("/users") {
        post("/") {
            val body = call.receiveText()
            call.respond(HttpStatusCode.OK, "ok")
        }
        patch("/") {
            val body = call.receiveText()
            call.respond(HttpStatusCode.OK, "ok")
        }
        post("/decode/") {
            call.respond(HttpStatusCode.OK, "User decoded")
        }
        get("/list/") {
            call.respond(
                HttpStatusCode.OK, """
                            {"users":[{"id":1,"ext_id":"+QKyZh1n/KA","key_code":"D7QMF3kZBciK4dmsp7lSSF9ZaXOHUswVKKa136e1jtU=","hkey_code":"Srithik Akeira 14","name":"test2412018081","status":"confirmed","activity":"offline"}]}
                        """.trimIndent()
            )
        }
        get("/my-contact/") {
            call.respond(HttpStatusCode.OK, "User contact")
        }
    }
}