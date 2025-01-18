package com.jmvsta.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

@Serializable
data class ServersDto(val servers: List<Server>)

@Serializable
data class Server(
    val id: String,
    val addr: String,
    val status: String,
    @SerialName("last_check") val lastCheck: String?,
) {
    companion object {
        private var currentId = 0

        fun create(addr: String, status: String, lastCheck: String? = LocalDateTime.now().format(dateFormat)): Server {
            val newId = currentId++
            return Server(newId.toString(), addr, status, lastCheck)
        }
    }
}