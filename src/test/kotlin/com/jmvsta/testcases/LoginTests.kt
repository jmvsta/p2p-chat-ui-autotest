package com.jmvsta.testcases

import com.jmvsta.computeSHA3_512
import com.jmvsta.entities.FormData
import com.jmvsta.mocks.MockServer
import com.jmvsta.mocks.modules.CallTracker
import com.jmvsta.poms.Login
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class LoginTests(driver: WebDriver, mock: MockServer): TestBase<Login>(driver, mock, Login(driver)) {

    @BeforeEach
    fun setUp() {
        CallTracker.clearCalls()
        driver.get("http://localhost:5173")
        (driver as JavascriptExecutor).executeScript("localStorage.removeItem('server')")
    }

    @Test
    @DisplayName("should login with correct credentials")
    fun loginSuccess() {
        pom.provideUsername("testUser")
        pom.providePassword("testPassword")
        val imagePath = Paths.get("src/test/resources/test.jpg").toAbsolutePath().toString()
        pom.provideFile(imagePath)
        pom.clickLoginButton()

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
    @DisplayName("should throw error on no login")
    fun noLoginError() {
        pom.providePassword("testPassword")
        val imagePath = Paths.get("src/test/resources/test.jpg").toAbsolutePath().toString()
        pom.provideFile(imagePath)
        pom.clickLoginButton()

        Thread.sleep(1000)

        val calls = CallTracker.getCalls()
        val initPwdCall = calls.find { it.uri.contains("/api/settings/init/?pwd=") && it.method == HttpMethod.Post }
        val createSettingsCall = calls.find { it.uri.contains("/api/settings/me/") && it.method == HttpMethod.Post }

        assert(initPwdCall == null && createSettingsCall == null)
        val errorPopup = driver.findElement(By.id("info-popup"))
        assert(errorPopup.text.contains( "All fields are required"))
    }

    @Test
    @DisplayName("should throw error on no password")
    fun noPasswordError() {
        pom.provideUsername("testUser")
        val imagePath = Paths.get("src/test/resources/test.jpg").toAbsolutePath().toString()
        pom.provideFile(imagePath)
        pom.clickLoginButton()

        Thread.sleep(1000)

        val calls = CallTracker.getCalls()
        val initPwdCall = calls.find { it.uri.contains("/api/settings/init/?pwd=") && it.method == HttpMethod.Post }
        val createSettingsCall = calls.find { it.uri.contains("/api/settings/me/") && it.method == HttpMethod.Post }

        assert(initPwdCall == null && createSettingsCall == null)
        val errorPopup = driver.findElement(By.id("info-popup"))
        assert(errorPopup.text.contains( "All fields are required"))
    }

    @Test
    @DisplayName("should throw error on no pic")
    fun noPicError() {
        pom.provideUsername("testUser")
        pom.providePassword("testPassword")
        pom.clickLoginButton()

        Thread.sleep(1000)

        val calls = CallTracker.getCalls()
        val initPwdCall = calls.find { it.uri.contains("/api/settings/init/?pwd=") && it.method == HttpMethod.Post }
        val createSettingsCall = calls.find { it.uri.contains("/api/settings/me/") && it.method == HttpMethod.Post }

        assert(initPwdCall == null && createSettingsCall == null)
        val errorPopup = driver.findElement(By.id("info-popup"))
        assert(errorPopup.text.contains( "All fields are required"))
    }
}
