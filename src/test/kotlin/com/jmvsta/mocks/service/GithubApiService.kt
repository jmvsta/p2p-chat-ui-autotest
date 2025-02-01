package com.jmvsta.mocks.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.server.plugins.NotFoundException
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Paths

class GithubApiService {

    private val client = HttpClient(CIO)
    private val buildJobUri = "https://api.github.com/repos/jmvsta/p2p-chat-ui/actions/workflows/release.yml/dispatches"
    private val latestBuildUri = "https://api.github.com/repos/jmvsta/p2p-chat-ui/releases/latest"
    private val staticScratchFolder = "build/static/"
    private val releaseUrlRegex = """"browser_download_url"\s*:\s*"([^"]+)"""".toRegex()
    private val githubToken: String = Files.readString(Paths.get("build/resources/test/github_token"))
    private val fileSystemService: FileSystemService = FileSystemService()


    init {
        getLatestBuild()
    }

//    fun runJob(url: String) {
//        val requestBody = Json.encodeToString(
//            mapOf("ref" to "main", "inputs" to mapOf("url" to url))
//        )
//        client.post()
//            .uri(buildJobUri)
//            .bodyValue(BodyInserters.fromValue(requestBody))
//            .retrieve()
//            .toBodilessEntity()
//            .subscribe {}
//    }

    fun getLatestBuild() = runBlocking {
        try {
            val latestReleaseJson: String = client.get(latestBuildUri) {
                headers {
                    append(HttpHeaders.Accept, "application/vnd.github.v3+json")
                    append(HttpHeaders.Authorization, "github_token $githubToken")
                }
            }.body()

            val downloadUrl = releaseUrlRegex.find(latestReleaseJson)?.groupValues?.get(1)
            if (downloadUrl.isNullOrEmpty()) {
                throw NotFoundException("latest version was not found")
            }

            val zipBytes: ByteArray = client.get(downloadUrl) {
                headers {
                    append(HttpHeaders.Accept, "application/octet-stream")
                    append(HttpHeaders.Authorization, "github_token $githubToken")
                }
            }.body()

            fileSystemService.extractZipFile(zipBytes.inputStream(), staticScratchFolder)
        } catch (e: Exception) {
            println("Error: ${e.message}")
        } finally {
            client.close()
        }
    }


}