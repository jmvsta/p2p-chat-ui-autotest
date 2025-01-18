package com.jmvsta.poms

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class Login(private val driver: WebDriver) {

    private val usernameInput = By.id("login-input")
    private val passwordInput = By.id("password-input")
    private val fileInput = By.id("file-input")
    private val loginButton = By.id("login-button")

    fun provideUsername(username: String) {
        driver.findElement(usernameInput).sendKeys(username)
    }

    fun providePassword(password: String) {
        driver.findElement(passwordInput).sendKeys(password)
    }

    fun provideFile(path: String) {
        driver.findElement(fileInput).sendKeys(path)
    }

    fun clickLoginButton() {
        driver.findElement(loginButton).click()
    }
}