package com.jmvsta.mocks.routes

import com.jmvsta.entities.MessageDto
import com.jmvsta.entities.MessagesListDto
import com.jmvsta.mocks.MockServer
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.messagesRoute(mock: MockServer) {
    route("/msgs") {
        post("/text/") {
            val jsonString = call.receiveText()
            val message = Json.decodeFromString<MessageDto>(jsonString)
            val list = mock.chatMessages.getOrPut(message.chatId) { mutableListOf() }
            list.add(com.jmvsta.entities.Message.toDto(message))
            call.respond(io.ktor.http.HttpStatusCode.OK)
        }
        get("/chat/") {
            val chatId = call.request.queryParameters["chat_id"]
            val offset = call.request.queryParameters["offset"]!!.toInt()
            val limit = call.request.queryParameters["limit"]!!.toInt()
            val messages =
                mock.chatMessages.getOrDefault(chatId, listOf()).sortedByDescending { it.id }.drop(offset)
                    .take(limit)
            call.respond(io.ktor.http.HttpStatusCode.OK, Json.encodeToString(MessagesListDto(messages)))
        }
    }
}