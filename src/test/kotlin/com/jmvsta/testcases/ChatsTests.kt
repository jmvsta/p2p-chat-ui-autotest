package com.jmvsta.testcases

import com.jmvsta.mocks.MockServer
import com.jmvsta.mocks.modules.CallTracker
import com.jmvsta.poms.Chats
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class ChatsTests(driver: WebDriver, mock: MockServer): TestBase<Chats>(driver, mock, Chats(driver)) {

    @BeforeEach
    fun setUp() {
        CallTracker.clearCalls()
        driver.get("http://localhost:5173")
        (driver as JavascriptExecutor).executeScript("localStorage.removeItem('server')")
    }

    @Test
    @DisplayName("should pom with correct credentials")
    fun loginSuccess() {
        TODO()
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
