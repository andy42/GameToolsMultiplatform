package com.jaehl.gameTool.common.ui.screens.home

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.screens.userDetails.UserDetailsRobot
import org.hamcrest.CoreMatchers
import org.junit.Assert


class HomeRobot(private val device: UiDevice) {

    private val navTitle = "Home"

    fun assertTitleHome() = apply {
        val textView = device.findObject(By.res( "navTitle"))
        Assert.assertThat(textView.getText(), CoreMatchers.`is`(CoreMatchers.equalTo(navTitle)))
    }

    fun isHomePage() : Boolean {
        val textView = device.findObject(By.res( "navTitle")) ?: return false
        return (textView.getText() == navTitle)
    }

    fun clickNavAccountDetailsAndTransition() : UserDetailsRobot {
        device.findObject(By.res( "navAccountDetails"))
            .clickAndWait(Until.newWindow(), 3000L)
        return UserDetailsRobot(device)
    }
}