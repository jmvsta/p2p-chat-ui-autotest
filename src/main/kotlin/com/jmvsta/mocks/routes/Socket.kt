package com.jmvsta.mocks.routes

import com.jmvsta.mocks.MockServer
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.ConcurrentHashMap

fun Route.socketRoute(mock: MockServer) {
    println("socketRoute")
    val rooms = ConcurrentHashMap<String, MutableSet<DefaultWebSocketServerSession>>()

    webSocket("/") {

        var currentRoom: String? = null

        for (frame in incoming) {
            if (frame !is Frame.Text) continue
            val text = frame.readText()

            val msg = Json.parseToJsonElement(text).jsonObject
            val type = msg["type"]?.jsonPrimitive?.content

            when (type) {

                "create_or_join" -> {
                    val room = msg["room"]!!.jsonPrimitive.content
                    val clients = rooms.computeIfAbsent(room) { ConcurrentHashMap.newKeySet() }

                    when (clients.size) {
                        0 -> {
                            clients.add(this)
                            currentRoom = room
                            send(Frame.Text("""{"type":"created","room":"$room"}"""))
                        }
                        1 -> {
                            clients.forEach { it.send(Frame.Text("""{"type":"join","room":"$room"}""")) }
                            clients.add(this)
                            currentRoom = room
                            send(Frame.Text("""{"type":"joined","room":"$room"}"""))
                        }
                        else ->
                            send(Frame.Text("""{"type":"full","room":"$room"}"""))
                    }
                }

                "message" -> {
                    val room = msg["room"]!!.jsonPrimitive.content
                    val data = msg["data"]!!.jsonPrimitive.content

                    rooms[room]?.forEach {
                        if (it != this)
                            it.send(Frame.Text(""""{"type":"message","room":"$room","data":"$data"}"""))
                    }
                }
            }
        }

        currentRoom?.let { rooms[it]?.remove(this) }
    }
}
