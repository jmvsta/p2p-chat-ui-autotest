package com.jmvsta.routes

import com.jmvsta.server.IServer
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.ConcurrentHashMap

sealed class RoomResult {
    data class Created(val room: String) : RoomResult()
    data class Joined(val room: String, val existingClient: DefaultWebSocketServerSession?) : RoomResult()
    data class Full(val room: String) : RoomResult()
    data class Error(val room: String, val message: String) : RoomResult()
}

fun Route.socketRoute(mock: IServer) {
    val rooms = ConcurrentHashMap<String, MutableSet<DefaultWebSocketServerSession>>()

    // Расширение для безопасной отправки
    suspend fun DefaultWebSocketServerSession.safeSend(text: String): Boolean {
        return try {
            send(Frame.Text(text))
            true
        } catch (e: Exception) {
            false
        }
    }

    webSocket("/ws") {
        val session = this
        var currentRoom: String? = null

        try {
            for (frame in incoming) {
                if (frame !is Frame.Text) continue
                val text = frame.readText()

                val msg = Json.parseToJsonElement(text).jsonObject
                val type = msg["type"]?.jsonPrimitive?.content
                val roomName = msg["room"]?.jsonPrimitive?.content

                when (type) {
                    "create_or_join" -> {
                        val room = roomName ?: continue

                        val result = synchronized(rooms) {
                            val clients = rooms.computeIfAbsent(room) { mutableSetOf() }

                            when (clients.size) {
                                0 -> {
                                    if (clients.add(session)) {
                                        currentRoom = room
                                        RoomResult.Created(room)
                                    } else {
                                        RoomResult.Error(room, "Failed to add client")
                                    }
                                }
                                1 -> {
                                    val existingClient = clients.firstOrNull()
                                    if (clients.add(session)) {
                                        currentRoom = room
                                        RoomResult.Joined(room, existingClient)
                                    } else {
                                        RoomResult.Error(room, "Failed to add client")
                                    }
                                }
                                else -> RoomResult.Full(room)
                            }
                        }

                        // Обрабатываем результат
                        when (result) {
                            is RoomResult.Created -> {
                                if (!session.safeSend("""{"type":"created","room":"${result.room}"}""")) {
                                    synchronized(rooms) { rooms[result.room]?.remove(session) }
                                    currentRoom = null
                                }
                            }
                            is RoomResult.Joined -> {
                                val success1 = result.existingClient?.safeSend("""{"type":"join","room":"${result.room}"}""") ?: true
                                val success2 = session.safeSend("""{"type":"joined","room":"${result.room}"}""")

                                if (!success1 || !success2) {
                                    synchronized(rooms) { rooms[result.room]?.remove(session) }
                                    currentRoom = null
                                }
                            }
                            is RoomResult.Full -> {
                                session.safeSend("""{"type":"full","room":"${result.room}"}""")
                            }
                            is RoomResult.Error -> {
                                println("Error in room ${result.room}: ${result.message}")
                            }
                        }
                    }

                    "message" -> {
                        val room = roomName ?: currentRoom ?: continue
                        val data = msg["data"]?.jsonPrimitive?.content ?: continue

                        val clients = synchronized(rooms) {
                            rooms[room]?.filter { it != session }?.toList()
                        }

                        clients?.forEach { client ->
                            if (!client.safeSend("""{"type":"message","room":"$room","message":"$data"}""")) {
                                synchronized(rooms) { rooms[room]?.remove(client) }
                            }
                        }
                    }

                    "close" -> {
                        val room = roomName ?: currentRoom
                        room?.let { roomName ->
                            synchronized(rooms) {
                                rooms[roomName]?.remove(session)
                                if (rooms[roomName]?.isEmpty() == true) {
                                    rooms.remove(roomName)
                                }
                            }
                            currentRoom = null
                            session.safeSend("""{"type":"left","room":"$roomName"}""")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("WebSocket error for session: ${e.message}")
        } finally {
            currentRoom?.let { roomName ->
                synchronized(rooms) {
                    rooms[roomName]?.remove(session)
                    if (rooms[roomName]?.isEmpty() == true) {
                        rooms.remove(roomName)
                    }
                }
            }
        }
    }
}