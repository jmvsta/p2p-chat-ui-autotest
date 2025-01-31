package com.jmvsta.poms

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

abstract class Pom(protected val driver: WebDriver) {

    fun click(byId: By) = find(byId).click()
    fun find(by: By): WebElement = driver.findElement(by)
    fun findAll(by: By): List<WebElement> = driver.findElements(by)
    fun type(by: By, text: String) = driver.findElement(by).sendKeys(text)
    fun count(by: By): Int = findAll(by).size
}
