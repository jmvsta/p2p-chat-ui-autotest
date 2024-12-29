package com.jmvsta.tests

import com.jmvsta.computeSHA3_512
import com.jmvsta.entities.FormData
import com.jmvsta.mocks.MockServer
import com.jmvsta.mocks.modules.CallTracker
import com.jmvsta.poms.LoginSegment
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import java.nio.file.Paths

class LoginTests {

    companion object {
        private lateinit var driver: WebDriver

        private lateinit var loginSegment: LoginSegment
        private lateinit var mock: MockServer

        @BeforeAll
        @JvmStatic
        fun init() {
            mock = MockServer(8080)
            mock.start()

            driver = ChromeDriver()
            driver.get("http://localhost:5173")
            val jsExecutor = driver as JavascriptExecutor
            jsExecutor.executeScript("localStorage.removeItem('server');")

            loginSegment = LoginSegment(driver)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            driver.quit()
            mock.stop()
        }
    }

    @BeforeEach
    fun setUp() {
        CallTracker.clearCalls()
        driver.get("http://localhost:5173")
    }

    @Test
    fun `should login with correct credentials`() {
        loginSegment.provideUsername("testUser")
        loginSegment.providePassword("testPassword")
        val imagePath = Paths.get("src/test/resources/test.jpg").toAbsolutePath().toString()
        loginSegment.provideFile(imagePath)
        loginSegment.clickLoginButton()

        Thread.sleep(1000)

        val calls = CallTracker.getCalls()
        val initPwdCall = calls.find { it.uri.contains("/api/settings/init/?pwd=") && it.method == HttpMethod.Post }

        assert(initPwdCall != null)
        assert(initPwdCall?.queryParameters?.get("pwd").equals(computeSHA3_512("testPassword")))


        val createSettingsCall = calls.find { it.uri.contains("/api/settings/me/") && it.method == HttpMethod.Post }

        assert(createSettingsCall != null)
        val body = Json.decodeFromString<FormData>(createSettingsCall?.body!!)
        assert(body.formData["name"] == "testUser")
        assert(body.files[0] == "test.jpg")

        assertThrows<org.openqa.selenium.NoSuchElementException> { driver.findElement(By.id("info-popup")) }
        assertDoesNotThrow { driver.findElement(By.id("server-component")) }
    }

    @Test
    fun `should throw error on no login`() {
        loginSegment.providePassword("testPassword")
        val imagePath = Paths.get("src/test/resources/test.jpg").toAbsolutePath().toString()
        loginSegment.provideFile(imagePath)
        loginSegment.clickLoginButton()

        Thread.sleep(1000)

        val calls = CallTracker.getCalls()
        val initPwdCall = calls.find { it.uri.contains("/api/settings/init/?pwd=") && it.method == HttpMethod.Post }
        val createSettingsCall = calls.find { it.uri.contains("/api/settings/me/") && it.method == HttpMethod.Post }

        assert(initPwdCall == null && createSettingsCall == null)
        val errorPopup = driver.findElement(By.id("info-popup"))
        assert(errorPopup.text.contains( "All fields are required"))
    }

    @Test
    fun `should throw error on no password`() {
        loginSegment.provideUsername("testUser")
        val imagePath = Paths.get("src/test/resources/test.jpg").toAbsolutePath().toString()
        loginSegment.provideFile(imagePath)
        loginSegment.clickLoginButton()

        Thread.sleep(1000)

        val calls = CallTracker.getCalls()
        val initPwdCall = calls.find { it.uri.contains("/api/settings/init/?pwd=") && it.method == HttpMethod.Post }
        val createSettingsCall = calls.find { it.uri.contains("/api/settings/me/") && it.method == HttpMethod.Post }

        assert(initPwdCall == null && createSettingsCall == null)
        val errorPopup = driver.findElement(By.id("info-popup"))
        assert(errorPopup.text.contains( "All fields are required"))
    }

    @Test
    fun `should throw error on no pic`() {
        loginSegment.provideUsername("testUser")
        loginSegment.providePassword("testPassword")
        loginSegment.clickLoginButton()

        Thread.sleep(1000)

        val calls = CallTracker.getCalls()
        val initPwdCall = calls.find { it.uri.contains("/api/settings/init/?pwd=") && it.method == HttpMethod.Post }
        val createSettingsCall = calls.find { it.uri.contains("/api/settings/me/") && it.method == HttpMethod.Post }

        assert(initPwdCall == null && createSettingsCall == null)
        val errorPopup = driver.findElement(By.id("info-popup"))
        assert(errorPopup.text.contains( "All fields are required"))
    }
}
