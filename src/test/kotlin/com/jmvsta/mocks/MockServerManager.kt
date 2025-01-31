package com.jmvsta.mocks

import java.io.File
import java.nio.file.NotDirectoryException

object MockServerManager {

    private val mockServers: MutableMap<Int, MockServer> = HashMap()

    fun create(port: Int): MockServer {
        createStatic(port)
        return mockServers.getOrPut(port) { MockServer(port).apply { start() } }
    }

    fun createStatic(port: Int) {
        val targetDir = File("build/resources/test/static$port")
        if (!targetDir.exists()) {
            targetDir.mkdir()
        }
        val sourceDir = File("build/resources/test/static")
        if (!sourceDir.exists()) {
            throw NotDirectoryException("no such dir")
        }
        sourceDir.listFiles()?.forEach { file ->
            file.copyTo(File(targetDir, file.name), overwrite = true)
        }
        val jsFile = File("build/resources/test/static$port/main.js")
        if (!sourceDir.exists()) {
            throw NotDirectoryException("no js file")
        }
        val content = jsFile.readText().replace("http://localhost:8080", "http://localhost:$port")
        jsFile.writeText(content)
    }

    fun detach(vararg ports: Int) {
        ports.forEach { port -> mockServers.remove(port)?.stop() }
    }
}