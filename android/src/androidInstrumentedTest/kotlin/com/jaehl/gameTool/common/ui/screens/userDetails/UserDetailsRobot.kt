package com.jaehl.gameTool.common.ui.screens.userDetails

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.screens.login.LoginScreenRobot
import org.hamcrest.CoreMatchers
import org.junit.Assert

class UserDetailsRobot(
    private val device: UiDevice,
    private val timeout : Long
) {

    val logoutButton = "logout"
    fun assertTitleUserDetails() = apply {
        val textView = device.findObject(By.res( "navTitle"))
        Assert.assertThat(textView.getText(), CoreMatchers.`is`(CoreMatchers.equalTo("User")))
    }
    fun clickLogoutAndTransition() : LoginScreenRobot {
        device.findObject(By.res( logoutButton))
            .clickAndWait(Until.newWindow(), timeout)
        return LoginScreenRobot(device, timeout)
    }
}