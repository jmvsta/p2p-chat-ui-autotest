package com.jmvsta.mocks

import com.jmvsta.entities.Chat
import com.jmvsta.entities.ExtUser
import com.jmvsta.entities.Message
import com.jmvsta.entities.Server
import com.jmvsta.entities.StatusDto
import com.jmvsta.mocks.modules.module
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine


class MockServer(val port: Int = 8080) {

    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null
    var chatMessages: MutableMap<String, MutableList<Message>> = mutableMapOf()
    var servers: MutableList<Server> = mutableListOf()
    var apiInited: StatusDto = StatusDto("test", false)
    var contacts: MutableList<ExtUser> = mutableListOf()
    var chats: MutableList<Chat> = mutableListOf()
    lateinit var me: ExtUser

    fun start() {
        server = embeddedServer(Netty, port = port) {
            module(this@MockServer, "static$port")
        }.start(wait = false)
    }

    fun stop() {
        server?.stop(1000, 1000)
        server = null
    }
}

//fun main() {
//    val mockServer = MockServerManager.create(8080)
//
//    mockServer.apiInited = StatusDto("test", true)
//    mockServer.servers.add(Server.create("http://localhost:8080", "active"))
//    mockServer.me = ExtUser.create("me", "code", "hkeyCode", "", "")
//    val user1 = ExtUser.create(
//        name = "test1", pic = "test",
//        keyCode = null,
//        hkeyCode = null,
//        status = null
//    )
//    val user2 = ExtUser.create(
//        name = "test2", pic = "test",
//        keyCode = null,
//        hkeyCode = null,
//        status = null
//    )
//
//    mockServer.contacts.addAll(mutableListOf(user1, user2))
//
//    val chatTest1 = Chat.create("test1", mutableListOf(user1), true)
//    val chatTest2 = Chat.create("test2", mutableListOf(user2), true)
//    val groupChat1 = Chat.create("groupchat1", mutableListOf(user1), false)
//
//    mockServer.chats.addAll(mutableListOf(chatTest1, chatTest2, groupChat1))
//
//    val mockServer1 = MockServerManager.create(8081)
//
//    mockServer1.contacts.addAll(mutableListOf(user1, user2))
//    mockServer1.chats.addAll(mutableListOf(chatTest1, chatTest2, groupChat1))
//
//    Runtime.getRuntime().addShutdownHook(Thread {
//        println("Stopping servers...")
//        MockServerManager.detach(8080, 8081)
//    })
//
//    Thread.currentThread().join()
//}

