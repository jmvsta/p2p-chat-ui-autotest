package com.jmvsta.poms

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class ServersSegment (private val driver: WebDriver) {

    private val usernameInput = By.id("username")
    private val passwordInput = By.id("password")
    private val loginButton = By.id("loginButton")

    fun enterUsername(username: String) {
        driver.findElement(usernameInput).sendKeys(username)
    }

    fun enterPassword(password: String) {
        driver.findElement(passwordInput).sendKeys(password)
    }

    fun clickLoginButton() {
        driver.findElement(loginButton).click()
    }
}