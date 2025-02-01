package com.jmvsta.scenarios

import com.jmvsta.mocks.MockClient
import com.jmvsta.mocks.MockServer
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
class Scenario01: Scenario {

    private lateinit var driver: ChromeDriver
    private val mockServer: MockServer = MockServer()
    private val mockClient: MockClient = mockServer.addClients("http://localhost:8080")[0]
    private lateinit var loginTests: LoginTests
    private lateinit var serverTests: ServerTests

    @BeforeAll
    fun setUp() {
        driver = ChromeDriver()
        loginTests = LoginTests(driver, mockClient)
        serverTests = ServerTests(driver, mockClient)
    }

    @AfterAll
    fun tearDown() {
        driver.quit()
        mockServer.detachClients("http://localhost:8080")
    }

    @TestFactory
    @DisplayName("Test login, select server, ...")
    fun runTests(): MutableList<DynamicTest> {
        return mutableListOf(
            DynamicTest.dynamicTest("Tests login") {
                runWithLifecycle(loginTests) { loginTests.loginSuccess() }
            },
            DynamicTest.dynamicTest("Test select server") {
                runWithLifecycle(serverTests) { serverTests.selectFirstServerCorrectly() }
            }
        )
    }
}