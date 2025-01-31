package com.jmvsta.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Chat(
    val id: String,
    val name: String,
    val read: Boolean,
    @SerialName("last_msg_txt") val lastMsgTxt: String?,
    @SerialName("last_msg_user") val lastMsgUser: String?,
    @SerialName("last_active") val lastActive: String,
    val uid: String?
) {

    companion object {
        private val chatDetails = mutableMapOf<String, ChatDetails>()
        private var currentId = 0

        fun create(
            name: String,
            participants: MutableList<ExtUser>? = mutableListOf(),
            private: Boolean,
            uid: String? = UUID.randomUUID().toString()
        ): Chat {
            val newId: String = if (private) "U${participants!![0].id}" else "C${currentId++}"
            val chat = Chat(newId, name, false, null, null, "", uid)
            chatDetails[newId] = ChatDetails(newId, name, null, participants?.map(ExtUser::id)?.toMutableList())
            return chat
        }

        fun findDetails(id: String): ChatDetails? {
            return chatDetails[id]
        }
    }
}

@Serializable
data class ChatDetails(
    val id: String,
    val name: String,
    @SerialName("last_active") val lastActive: String?,
    val participants: MutableList<Int>? = mutableListOf()
)

@Serializable data class ChatListDto(val chats: MutableList<Chat>)
@Serializable data class ChatDto(val name: String, val participants: MutableList<Int>)