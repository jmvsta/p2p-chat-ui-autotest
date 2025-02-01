package com.jmvsta.testcases

import com.jmvsta.entities.Chat
import com.jmvsta.entities.ExtUser
import com.jmvsta.entities.Server
import com.jmvsta.entities.StatusDto
import com.jmvsta.mocks.MockClient
import com.jmvsta.mocks.modules.CallTracker
import com.jmvsta.poms.Chats
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class ChatsTests(driver: WebDriver, mock: MockClient): TestCase<Chats>(driver, mock, Chats(driver)) {

    @BeforeEach
    fun setUp() {
        mock.apiInited = StatusDto("test", true)
        CallTracker.clearCalls()
        val server = Server.create("http://testserver:8080", "active")
        mock.servers.add(server)

        val serverString = Json.encodeToString(server)
        val me = ExtUser.create("me", "code", "hkeyCode", "testpic", "")

        mock.me = me
        driver.get(mock.url)
        (driver as JavascriptExecutor).executeScript("localStorage.setItem('server', JSON.stringify(JSON.parse(arguments[0])))", serverString)
        driver.get(mock.url)
    }

    @Test
    @DisplayName("should open chat window on chat selected")
    fun selectChat() {
        val user1 = ExtUser.create(name = "test2", pic = "test")
        val chatUser1 = Chat.create("testUser", mutableListOf(user1), true)
        mock.chats.add(chatUser1)

        Thread.sleep(6000)
        pom.clickChatByUserName(user1.name)
        assertEquals(1, pom.getAmountOfChats())
        assertTrue { pom.isChatWindowOpened() }
    }

    @Test
    @DisplayName("should throw error on no pom")
    fun noLoginError() {
        TODO()
    }

    @Test
    @DisplayName("should throw error on no password")
    fun noPasswordError() {
        TODO()
    }

    @Test
    @DisplayName("should throw error on no pic")
    fun noPicError() {
        TODO()
    }
}
