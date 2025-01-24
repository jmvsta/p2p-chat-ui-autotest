package com.jmvsta.mocks.routes

import com.jmvsta.entities.Chat
import com.jmvsta.entities.ChatDto
import com.jmvsta.entities.ChatListDto
import com.jmvsta.mocks.MockServer
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.AttributeKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.chatsRoute(mock: MockServer) {
    route("/chats") {
        get("/list/") {
            val offset = call.request.queryParameters["offset"]
            val limit = call.request.queryParameters["limit"]
            val filterBanned = call.request.queryParameters["filter_banned"]
            call.respond(HttpStatusCode.OK, Json.encodeToString(ChatListDto(mock.chats)))
        }
        get("/details/") {
            val id = call.request.queryParameters["id"]!!
            call.respond(HttpStatusCode.OK, Json.encodeToString(Chat.findDetails(id)))
        }
        post("/") {
            val jsonString = call.attributes.getOrNull(AttributeKey<String>("cachedBody"))!!
            val chat = Json.decodeFromString<ChatDto>(jsonString)
            val participants = chat.participants.mapNotNull  { id ->
                mock.contacts.find { contact ->
                    contact.id == id
                }
            }.toMutableList()
            mock.chats.add(Chat.create(chat.name, participants, false))
            call.respond(HttpStatusCode.OK, "ok")
        }
    }
}