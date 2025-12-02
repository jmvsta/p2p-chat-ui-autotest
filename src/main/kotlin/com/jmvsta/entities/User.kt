package com.jmvsta.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ExtUser(
    val id: String,
    @SerialName("ext_id") val extId: String?,
    val name: String,
    @SerialName("key_code") val keyCode: String?,
    @SerialName("hkey_code") val hkeyCode: String?,
    val pic: String,
    val status: String?,
    val activity: String?,
) {
    companion object {
        fun create(name: String, keyCode: String?, hkeyCode: String?, pic: String, status: String?): ExtUser {
            return ExtUser(UUID.randomUUID().toString(), null, name, keyCode, hkeyCode, pic, status, null)
        }
    }
}

@Serializable data class UserAddDto(val name: String, val contact: String)
@Serializable data class UserUpdateMeDto(val name: String, val pic: String)
@Serializable data class UserUpdateDto(val id: Int, val name: String, val status: String)
