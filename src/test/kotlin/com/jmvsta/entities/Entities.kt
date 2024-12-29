package com.jmvsta.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

@Serializable
data class Message(
    val id: Int,
    val sender: String?,
    val time: String?,
    val payload: Payload,
    val read: Boolean,
    val received: Boolean,
) {
    companion object {
        private var currentId = 0

        fun create(
            sender: String?,
            time: String? = LocalDateTime.now().format(dateFormat),
            read: Boolean,
            received: Boolean,
            type: String,
            text: String,
            downloaded: Boolean,
        ): Message {
            val newId = currentId++
            val payload = Payload(type, text, downloaded, null)
            return Message(newId, sender, time, payload, read, received)
        }

        fun toDto(message: MessageDto): Message {
            val newId = currentId++
            val payload = Payload("text", message.text, false, null)
            return Message(newId, null, LocalDateTime.now().format(dateFormat), payload, read = false, received = false)
        }
    }
}

@Serializable
data class FormData(val formData: MutableMap<String, String>, val files: MutableList<String>)

@Serializable
data class MessageDto(@SerialName("chat_id") val chatId: String, val text: String)

@Serializable
data class StatusDto(@SerialName("log_prefix") val logPrefix: String, val inited: Boolean)

@Serializable
data class ServersDto(val servers: Server)

@Serializable
data class Server(
    val id: Int,
    val addr: String,
    val status: String,
    @SerialName("last_check") val lastCheck: String?,
) {
    companion object {
        private var currentId = 0

        fun create(addr: String, status: String, lastCheck: String? = LocalDateTime.now().format(dateFormat)): Server {
            val newId = currentId++
            return Server(newId, addr, status, lastCheck)
        }
    }
}

@Serializable
data class MessagesListDto(val msgs: List<Message>)

@Serializable
data class Payload(val type: String, val data: String, val downloaded: Boolean, val path: String?)

