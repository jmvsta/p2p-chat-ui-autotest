package com.jmvsta

import com.jmvsta.server.ServerManager
import com.jmvsta.server.MockServerType

fun main() {
    ServerManager.create(8080)
    ServerManager.create(8081)
    ServerManager.create(8082, MockServerType.WS)
    Runtime.getRuntime().addShutdownHook(Thread {
        ServerManager.detach()
    })
    Thread.currentThread().join()
}