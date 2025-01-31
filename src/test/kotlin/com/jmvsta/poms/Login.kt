package com.jmvsta.poms

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class Login(driver: WebDriver) : Pom(driver) {

    private val usernameInput = By.id("login-input")
    private val passwordInput = By.id("password-input")
    private val fileInput = By.id("file-input")
    private val loginButton = By.id("login-button")

    fun provideUsername(username: String) = type(usernameInput, username)
    fun providePassword(password: String) = type(passwordInput, password)
    fun provideFile(path: String) = type(fileInput, path)
    fun clickLoginButton() = click(loginButton)
}