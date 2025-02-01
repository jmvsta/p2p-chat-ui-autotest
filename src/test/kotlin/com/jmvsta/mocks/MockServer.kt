package com.jmvsta.mocks

class MockServer {

    private val clients: MutableMap<String, MockClient> = HashMap()

    fun addClients(vararg urls: String): List<MockClient> {
        return urls.map { url ->
            clients.getOrPut(url) {
                MockClient(url).apply { start() }
            }
        }
    }

    fun detachClients(vararg urls: String) {
        urls.forEach { url -> clients.remove(url)?.stop() }
    }
}