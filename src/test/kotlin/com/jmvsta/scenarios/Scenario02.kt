package com.jmvsta.scenarios

import com.jmvsta.entities.Chat
import com.jmvsta.entities.ExtUser
import com.jmvsta.entities.Server
import com.jmvsta.entities.StatusDto
import com.jmvsta.mocks.MockClient
import com.jmvsta.mocks.MockServer
import com.jmvsta.mocks.service.GithubApiService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.chrome.ChromeDriver
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Scenario02 : Scenario {

    private val githubApiService = GithubApiService()
    private lateinit var driver1: ChromeDriver
    private lateinit var driver2: ChromeDriver
    private lateinit var mock1: MockClient
    private lateinit var mock2: MockClient
    private val mockServer = MockServer()
    private val hosts = arrayOf("http://localhost:8080", "http://localhost:8081")


//    fun startChromeDriver(port: Int, userDataDir: String): WebDriver {
//        val service = ChromeDriverService.Builder()
//            .usingPort(port)
//            .build()
//        service.start()
//
//        val options = ChromeOptions()
//        options.addArguments("--remote-debugging-port=${port + 1000}")
//        options.addArguments("--user-data-dir=$userDataDir")
//        options.addArguments("--profile-directory=Profile$port")
//
//        return ChromeDriver(service, options)
//    }

    @BeforeAll
    fun setUp() {
        driver1 = ChromeDriver()
        driver2 = ChromeDriver()
        val clients = mockServer.addClients(*hosts)
        mock1 = clients[0]
        mock1.apiInited = StatusDto("test0", true)
        mock1.servers.add(Server.create("http://testserver:8080", "active"))
        mock1.me = ExtUser.create("me", "code", "hkeyCode", "", "")
        val user1 = ExtUser.create(name = "test1", pic = "test")
        val user2 = ExtUser.create(name = "test2", pic = "test")

        mock1.contacts.addAll(mutableListOf(user2))
        val uid = UUID.randomUUID().toString()
        val chatTest1 = Chat.create("test2", mutableListOf(user2), true, uid)
        val chatTest2 = Chat.create("test1", mutableListOf(user1), true, uid)

        mock1.chats.addAll(mutableListOf(chatTest1))

        mock2 = clients[1]
        mock2.apiInited = StatusDto("test1", true)
        mock2.servers.add(Server.create("http://testserver:8080", "active"))
        mock2.contacts.addAll(mutableListOf(user1))
        mock2.chats.addAll(mutableListOf(chatTest2))
        Thread.sleep(6000)
    }

    @AfterAll
    fun tearDown() {
        driver1.quit()
        driver2.quit()
        mockServer.detachClients(*hosts)
    }

    @Test
    fun test() {
        driver1.get("http://localhost:8080")
        driver2.get("http://localhost:8081")


        Thread.sleep(10000)
    }

}