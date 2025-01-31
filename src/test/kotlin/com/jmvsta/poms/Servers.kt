package com.jmvsta.poms

import org.openqa.selenium.By
import org.openqa.selenium.By.ByClassName
import org.openqa.selenium.WebDriver

class Servers(driver: WebDriver) : Pom(driver) {

    private val infoPopupButtonOkClass = ByClassName("popup-button")
    private val inputId = By.ById("server-key-input")
    private val addServerButton = By.ById("add-server-button")
    private val autocompleteDropdownSelector = By.ByCssSelector(".MuiAutocomplete-popupIndicator")
    private val autocompleteId = By.ById("servers-autocomplete")
    private val autoCompleteOption = By.cssSelector("[id^='servers-autocomplete-option-']")

    private fun autocompleteOptionId(number: Int): By.ById =
        By.ById("servers-autocomplete-option-${number}")
    private fun autocompleteRemoveOptionSelector(number: Int): By.ByCssSelector =
        By.ByCssSelector("[data-testid^='remove-${number}']")

    fun clickSelectAndChooseOptionNo(number: Int) {
        click(autocompleteDropdownSelector)
        click(autocompleteOptionId(number))
    }

    fun clickSelectAndRemoveOptionNo(number: Int) {
        click(autocompleteDropdownSelector)
        click(autocompleteRemoveOptionSelector(number))
    }

    fun getOptionsCount(): Int {
        if (!find(autocompleteId).getDomAttribute("aria-expanded").toBoolean()) {
            click(autocompleteDropdownSelector)
        }
        return count(autoCompleteOption)
    }

    fun enterServerKey(key: String) = driver.findElement(inputId).sendKeys(key)
    fun clickAddServerButton() = click(addServerButton)
    fun clickInfoPopupOkButton() = click(infoPopupButtonOkClass)
}