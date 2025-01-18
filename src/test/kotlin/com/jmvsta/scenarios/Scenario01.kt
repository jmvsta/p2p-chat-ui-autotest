package com.jmvsta.scenarios

import com.jmvsta.mocks.MockServer
import com.jmvsta.mocks.MockServerManager
import com.jmvsta.testcases.LoginTests
import com.jmvsta.testcases.ServerTests
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.chrome.ChromeDriver

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Scenario01: Scenario() {

    private lateinit var driver: ChromeDriver
    private lateinit var mock: MockServer
    private lateinit var loginTests: LoginTests
    private lateinit var serverTests: ServerTests

    @BeforeAll
    fun setUp() {
        driver = ChromeDriver()
        mock = MockServerManager.create(8080)
        loginTests = LoginTests(driver, mock)
        serverTests = ServerTests(driver, mock)
    }

    @AfterAll
    fun tearDown() {
        driver.quit()
        MockServerManager.detach(8080)
    }

    @TestFactory
    @DisplayName("Test login, select server, ...")
    fun runTests(): MutableList<DynamicTest> {
        return mutableListOf(
            DynamicTest.dynamicTest("Tests login") {
                runWithLifecycle(loginTests) { loginTests.loginSuccess() }
            },
            DynamicTest.dynamicTest(" Test select server") {
                runWithLifecycle(serverTests) { serverTests.selectFirstServerCorrectly() }
            }
        )
    }
}