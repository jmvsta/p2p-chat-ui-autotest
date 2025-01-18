package com.jmvsta.testcases

import com.jmvsta.entities.Server
import com.jmvsta.entities.StatusDto
import com.jmvsta.mocks.MockServer
import com.jmvsta.mocks.modules.CallTracker
import com.jmvsta.poms.Servers
import io.ktor.http.HttpMethod
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class ServerTests(driver: WebDriver, mock: MockServer) : TestBase<Servers>(driver, mock, Servers(driver)) {

    @BeforeEach
    fun setUp() {
        mock.apiInited = StatusDto("test", true)
        CallTracker.clearCalls()
        mock.servers = mutableListOf(
            Server.create("localhost:0", "active"),
            Server.create("localhost:1", "active")
        )
        Thread.sleep(6000)
        driver.get("http://localhost:5173")
        val jsExecutor = driver as JavascriptExecutor
        jsExecutor.executeScript("localStorage.removeItem('server');")
    }

    @Test
    @DisplayName("should select first server from autocomplete 2 list correctly")
    fun selectFirstServerCorrectly() {
        pom.clickSelectAndChooseOptionNo(0)
        assertNotNull(driver.findElement(By.id("simple_menu")))
    }

    @Test
    @DisplayName("should throw error on no server key")
    fun errorNoServerKey() {
        pom.clickAddServerButton()
        val errorPopup = driver.findElement(By.id("info-popup"))
        assert(errorPopup.text.contains("Server key must be provided"))
    }

    @Test
    @DisplayName("should remove server from autocomplete correctly")
    fun removeServerCorrectly() {
        pom.clickSelectAndRemoveOptionNo(0)
        Thread.sleep(6000)
        val calls = CallTracker.getCalls()
        val removeServerCall = calls.find { it.uri.contains("/api/servers/?id=") && it.method == HttpMethod.Delete }
        assertNotNull(removeServerCall)

        assertEquals(1, mock.servers.size)
        assertNotEquals("0", mock.servers[0].id)
        assertEquals(1, pom.getOptionsCount())
    }

    @Test
    @DisplayName("should add server")
    fun addServerCorrectly() {
        mock.servers.clear()
        pom.enterServerKey("test-key")
        pom.clickAddServerButton()
        Thread.sleep(6000)
        val calls = CallTracker.getCalls()
        val addServerCall = calls.find { it.uri.contains("/api/servers/") && it.method == HttpMethod.Post }
        assertNotNull(addServerCall)
        pom.clickInfoPopupOkButton()
        assertEquals(1, mock.servers.size)
        assertEquals(1, pom.getOptionsCount())
    }
}
