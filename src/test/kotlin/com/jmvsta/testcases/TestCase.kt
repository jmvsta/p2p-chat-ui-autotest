package com.jmvsta.testcases

import com.jmvsta.mocks.MockClient
import com.jmvsta.poms.Pom
import org.openqa.selenium.WebDriver

abstract class TestCase<T: Pom>(val driver: WebDriver, val mock: MockClient, val pom: T)
