package com.jmvsta.mocks

object MockServerManager {

    private val mockServers: MutableMap<Int, MockServer> = HashMap()

    fun create(port: Int): MockServer {
        return mockServers.getOrPut(port) { MockServer(port).apply { start() } }
    }

    fun detach(port: Int) {
        mockServers.remove(port)?.stop()
    }
}