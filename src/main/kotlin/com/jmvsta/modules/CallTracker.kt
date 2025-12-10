package com.jmvsta.modules

import io.ktor.http.HttpMethod

object CallTracker {
    private val calls = mutableListOf<TrackedCall>()

    data class TrackedCall(
        val method: HttpMethod,
        val uri: String,
        val queryParameters: Map<String, String>,
        val body: String?
    )

    fun addCall(call: TrackedCall) {
        calls.add(call)
    }

    fun getCalls(): List<TrackedCall> = calls.toList()

    fun clearCalls() {
        calls.clear()
    }
}