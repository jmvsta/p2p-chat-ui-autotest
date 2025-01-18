package com.jmvsta.mocks

import com.jmvsta.entities.ExtUser
import com.jmvsta.entities.Message
import com.jmvsta.entities.Server
import com.jmvsta.entities.StatusDto
import com.jmvsta.mocks.modules.module
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine


class MockServer(private val port: Int = 8080) {

    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null
    var chatMessages: MutableMap<String, MutableList<Message>> = mutableMapOf()
    var servers: MutableList<Server> = mutableListOf()
    var apiInited: StatusDto = StatusDto("test", false)
    lateinit var me: ExtUser

    fun start() {
        server = embeddedServer(Netty, port = port) {
            module(this@MockServer)
        }.start(wait = false)
    }

    fun stop() {
        server?.stop(1000, 1000)
        server = null
    }
}

fun main() {
    val mockServer = MockServer(8080)
    mockServer.start()

    println("Mock server is running on port 8080. Press Ctrl+C to stop.")

    Runtime.getRuntime().addShutdownHook(Thread {
        println("Stopping the server...")
        mockServer.stop()
    })

    Thread.currentThread().join()
}

