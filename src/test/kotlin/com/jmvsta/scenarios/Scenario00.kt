package com.jmvsta.scenarios

import com.jmvsta.mocks.MockClient
import com.jmvsta.mocks.MockServer
import com.jmvsta.testcases.ChatsTests
import com.jmvsta.testcases.LoginTests
import com.jmvsta.testcases.ServerTests
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.chrome.ChromeDriver

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Scenario00 : Scenario {

    private val driver: ChromeDriver = ChromeDriver()
    private val mockServer: MockServer = MockServer()
    private val mockClient: MockClient = mockServer.addClients("http://localhost:8080")[0]

   @Nested
   inner class Scenario00LoginTests : LoginTests(driver, mockClient)
   @Nested
   inner class Scenario00ServerTests : ServerTests(driver, mockClient)
    @Nested
    inner class Scenario00ChatTests : ChatsTests(driver, mockClient)

    @AfterAll
    fun tearDown() {
        driver.quit()
        mockServer.detachClients("http://localhost:8080")
    }

}