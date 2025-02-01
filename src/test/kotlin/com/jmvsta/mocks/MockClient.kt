package com.jmvsta.mocks

import com.jmvsta.entities.Chat
import com.jmvsta.entities.ExtUser
import com.jmvsta.entities.Message
import com.jmvsta.entities.Server
import com.jmvsta.entities.StatusDto
import com.jmvsta.mocks.modules.module
import com.jmvsta.mocks.service.FileSystemService
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import java.net.URI


class MockClient(val url: String) {

    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null
    private val fileSystemService: FileSystemService = FileSystemService()
    var chatMessages: MutableMap<String, MutableList<Message>> = mutableMapOf()
    var servers: MutableList<Server> = mutableListOf()
    var apiInited: StatusDto = StatusDto("test", false)
    var contacts: MutableList<ExtUser> = mutableListOf()
    var chats: MutableList<Chat> = mutableListOf()
    lateinit var me: ExtUser


    fun start() {
        val uri = URI(url)
        fileSystemService.createStatic(url)
        server = embeddedServer(Netty, host = uri.host, port = uri.port) {
            module(this@MockClient, "static${url.replace("[:/.]".toRegex(), "")}")
        }.start(wait = false)
    }

    fun stop() {
        server?.stop(1000, 1000)
        server = null
    }
}
