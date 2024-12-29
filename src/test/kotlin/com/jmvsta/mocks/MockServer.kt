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
    internal val chatMessages: MutableMap<String, MutableList<Message>> = mutableMapOf()
    internal val servers: MutableList<Server> = mutableListOf()
    internal val apiInited: StatusDto = StatusDto("test", false)
    internal lateinit var me: ExtUser

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

