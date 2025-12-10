package com.jmvsta.server

enum class MockServerType {
    WS,
    HTTP
}

object ServerManager {

    private val mockServers: MutableMap<Int, IServer> = HashMap()

    fun create(port: Int, type: MockServerType = MockServerType.HTTP): IServer {
        val server = when (type) {
            MockServerType.HTTP -> HttpServer(port)
            MockServerType.WS -> MockWsServer(port)
        }
        return mockServers.getOrPut(port) { server.apply { start() } }
    }

    fun detach(vararg ports: Int) {
        if (ports.isEmpty()) {
            mockServers.apply { values.forEach { it.stop() } }
            mockServers.clear()
        } else {
            ports.forEach { port ->
                mockServers.remove(port)?.stop()
            }
        }
    }
}