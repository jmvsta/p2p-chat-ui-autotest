package com.jmvsta.routes

import com.jmvsta.entities.CallDto
import com.jmvsta.entities.Message
import com.jmvsta.entities.MessageDto
import com.jmvsta.entities.MessagesListDto
import com.jmvsta.server.IServer
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import java.util.*

fun Route.messagesRoute(mock: IServer) {
    route("/msgs") {
        post("/text/") {
            val jsonString = call.receiveText()
            val message = Json.decodeFromString<MessageDto>(jsonString)
            val list = mock.chatMessages.getOrPut(message.chatId) { mutableListOf() }
            list.add(Message.fromTextMsgDto(message))
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
        post("/call/") {
//            val jsonString = call.receiveText()
            val jsonString = call.attributes.getOrNull(AttributeKey<String>("cachedBody"))
            val message = Json.decodeFromString<CallDto>(jsonString!!)

            val list = mock.chatMessages.getOrPut(message.chatId) { mutableListOf() }
            list.add(Message.fromCallMsgDto(message))
            call.respond(HttpStatusCode.OK, UUID.randomUUID().toString())
        }
    }
}