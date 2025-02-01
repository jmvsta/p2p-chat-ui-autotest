package com.jmvsta.mocks.routes

import com.jmvsta.entities.Message
import com.jmvsta.entities.MessageDto
import com.jmvsta.entities.MessagesListDto
import com.jmvsta.mocks.MockClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.AttributeKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.messagesRoute(mock: MockClient) {
    route("/msgs") {
        post("/text/") {
            val jsonString = call.attributes.getOrNull(AttributeKey<String>("cachedBody"))!!
            val message = Json.decodeFromString<MessageDto>(jsonString)
            val list = mock.chatMessages.getOrPut(message.chatId) { mutableListOf() }
            list.add(Message.toDto(message))
            call.respond(HttpStatusCode.OK)
        }
        post("/file/") {
            val jsonString = call.attributes.getOrNull(AttributeKey<String>("cachedBody"))!!
            val message = Json.decodeFromString<MessageDto>(jsonString)
            val list = mock.chatMessages.getOrPut(message.chatId) { mutableListOf() }
            list.add(Message.toDto(message))
            call.respond(HttpStatusCode.OK)
        }
        get("/chat/") {
            val chatId = call.request.queryParameters["chat_id"]
            val offset = call.request.queryParameters["offset"]!!.toInt()
            val limit = call.request.queryParameters["limit"]!!.toInt()
            val messages =
                mock.chatMessages.getOrDefault(chatId, listOf()).sortedByDescending { it.id }.drop(offset)
                    .take(limit)
            call.respond(HttpStatusCode.OK, Json.encodeToString(MessagesListDto(messages)))
        }
    }
}