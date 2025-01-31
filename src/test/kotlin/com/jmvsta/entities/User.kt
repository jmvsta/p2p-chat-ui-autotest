package com.jmvsta.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ExtUser(
    val id: Int,
    @SerialName("ext_id") val extId: String?,
    val name: String,
    @SerialName("key_code") val keyCode: String?,
    @SerialName("hkey_code") val hkeyCode: String?,
    val pic: String,
    val status: String?,
    val activity: String?,
) {
    companion object {
        private var currentId: Int = 0

        fun create(
            name: String, keyCode: String? = null, hkeyCode: String? = null, pic: String, status: String? = null,
        ): ExtUser {
            return ExtUser(currentId++, UUID.randomUUID().toString(), name, keyCode, hkeyCode, pic, status, null)
        }
    }
}

@Serializable
data class UserListDto(val users: MutableList<ExtUser>)
@Serializable
data class UserAddDto(val name: String, val contact: String)
@Serializable
data class UserUpdateMeDto(val name: String, val pic: String)
@Serializable
data class UserUpdateDto(val id: Int, val name: String, val status: String)
