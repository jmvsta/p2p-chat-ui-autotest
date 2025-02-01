package com.jmvsta.mocks.routes

import com.jmvsta.entities.UserListDto
import com.jmvsta.mocks.MockClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.usersRoute(mock: MockClient) {
    route("/users") {
        post("/") {
            val body = call.receiveText()
            call.respond(HttpStatusCode.OK, "ok")
        }
        patch("/") {
            val body = call.receiveText()
            call.respond(HttpStatusCode.OK, "ok")
        }
        delete("/") {
            val id = call.request.queryParameters["id"]!!.toInt()
            mock.contacts.removeIf { contact -> contact.id == id }
            call.respond(HttpStatusCode.OK, "ok")
        }
        post("/decode/") {
            call.respond(HttpStatusCode.OK, "User decoded")
        }
        get("/list/") {
            call.respond(HttpStatusCode.OK, Json.encodeToString(UserListDto(mock.contacts)))
        }
        get("/my-contact/") {
            call.respond(HttpStatusCode.OK, "User contact")
        }
    }
}