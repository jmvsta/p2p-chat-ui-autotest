package com.jmvsta.poms

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver


class Chats(driver: WebDriver) : Pom(driver) {

    private val buttonsClass = By.className("MuiListItemButton-root")
    private val chatNameClass = By.className("MuiTypography-root")
    private val chatsAppToolbarClass = By.className("chat-toolbar")

    fun getAmountOfChats(): Int {
        return driver.findElements(buttonsClass).size
    }

    fun clickChatByUserName(name: String) {
        find(buttonsClass)
            .findElements(chatNameClass)
            .find { el -> el.text == "testUser" }?.click()
    }

    fun isChatWindowOpened(): Boolean {
        return driver.findElements(chatsAppToolbarClass).size > 0
    }
}
