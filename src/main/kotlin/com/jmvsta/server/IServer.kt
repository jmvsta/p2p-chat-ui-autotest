package com.jmvsta.server

import com.jmvsta.entities.ExtUser
import com.jmvsta.entities.Message
import com.jmvsta.entities.Server
import com.jmvsta.entities.StatusDto
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.netty.NettyApplicationEngine

abstract class IServer(protected val port: Int, protected val host: String) {
    protected var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null
    var chatMessages: MutableMap<String, MutableList<Message>> = mutableMapOf()
    var servers: MutableList<Server> = mutableListOf()
    var apiInited: StatusDto = StatusDto("test", false)
    lateinit var me: ExtUser
    abstract fun start()
    abstract fun stop()
}