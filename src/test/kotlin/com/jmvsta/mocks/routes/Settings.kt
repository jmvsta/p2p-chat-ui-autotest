package com.jmvsta.mocks.routes

import com.jmvsta.entities.ExtUser
import com.jmvsta.entities.FormData
import com.jmvsta.mocks.MockServer
import com.jmvsta.parseFormData
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.AttributeKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun Route.settingsRoute(mock: MockServer) {
    route("/settings") {
        get("/me/") {
            val body = call.receiveText()
            call.respond(HttpStatusCode.OK, Json.encodeToString(mock.me).trimIndent())
        }

        post("/me/") {
            val cachedBody = call.attributes.getOrNull(AttributeKey<String>("cachedBody"))
            val parameters = Json.decodeFromString<FormData>(cachedBody!!)
            val name = parameters.formData["name"]!!
            val pic = parameters.files[0]
            mock.me = ExtUser.create(name = name, null, null, pic = pic, null)
            call.respond(HttpStatusCode.OK, "ok")
        }

        get("/status/") {
            call.respond(HttpStatusCode.OK, Json.encodeToString(mock.apiInited))
        }

        post("/init/") {
            val password = call.request.queryParameters["pwd"]
            call.respond("ok")
        }
    }
}