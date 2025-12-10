package com.jmvsta.server

import com.jmvsta.modules.module
import io.ktor.server.engine.*
import io.ktor.server.netty.*

class HttpServer(port: Int, host: String = "0.0.0.0") : IServer(port, host) {

    override fun start() {
        println("Starting ws server on port $port")
        server = embeddedServer(Netty, port = port, host = host) {
            module(this@HttpServer)
        }.start(wait = false)
    }

    override fun stop() {
        server?.stop(1000, 1000)
        server = null
    }
}

