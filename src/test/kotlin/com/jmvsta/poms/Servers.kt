package com.jmvsta.poms

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class Servers(private val driver: WebDriver) {

    private val infoPopup = By.className("popup-button")
    private val keyInput = By.id("server-key-input")
    private val addServerButton = By.id("add-server-button")

    private fun clickSelect() {
        driver.findElement(By.cssSelector(".MuiAutocomplete-popupIndicator")).click()
    }

    fun clickSelectAndChooseOptionNo(number: Int) {
        clickSelect()
        driver.findElement(By.id("servers-autocomplete-option-${number}")).click()
    }

    fun clickSelectAndRemoveOptionNo(number: Int) {
        clickSelect()

        val server = driver.findElement(By.id("servers-autocomplete-option-${number}"))
        server.findElement(By.cssSelector("[data-testid^='remove-']")).click()
    }

    fun getOptionsCount(): Int {
        if (!driver.findElement(By.id("servers-autocomplete")).getDomAttribute("aria-expanded").toBoolean())
            clickSelect()
        return driver.findElements(By.cssSelector("[id^='servers-autocomplete-option-']")).size
    }

    fun enterServerKey(key: String) {
        driver.findElement(keyInput).sendKeys(key)
    }

    fun clickAddServerButton() {
        driver.findElement(addServerButton).click()
    }

    fun clickInfoPopupOkButton() {
        driver.findElement(infoPopup).click()
    }
}