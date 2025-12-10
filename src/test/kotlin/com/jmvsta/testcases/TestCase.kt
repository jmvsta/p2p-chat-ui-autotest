package com.jmvsta.testcases

import com.jmvsta.server.MockServer
import org.openqa.selenium.WebDriver

abstract class TestBase<T>(val driver: WebDriver, val mock: MockServer, val pom: T)
