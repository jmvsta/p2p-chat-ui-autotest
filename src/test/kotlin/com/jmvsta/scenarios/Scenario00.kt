package com.jmvsta.scenarios

import com.jmvsta.mocks.MockServer
import com.jmvsta.mocks.MockServerManager
import com.jmvsta.testcases.LoginTests
import com.jmvsta.testcases.ServerTests
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.chrome.ChromeDriver

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Scenario00 : Scenario() {

    private val driver: ChromeDriver = ChromeDriver()
    private val mock: MockServer = MockServerManager.create(8080)

    @Nested
    inner class Scenario00LoginTests : LoginTests(driver, mock)
    @Nested
    inner class Scenario00ServerTests : ServerTests(driver, mock)

    @AfterAll
    fun tearDown() {
        driver.quit()
        MockServerManager.detach(8080)
    }

}