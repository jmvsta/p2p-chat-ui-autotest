package com.jmvsta.testcases

import com.jmvsta.mocks.MockServer
import com.jmvsta.poms.Pom
import org.openqa.selenium.WebDriver

abstract class TestCase<T: Pom>(val driver: WebDriver, val mock: MockServer, val pom: T)
