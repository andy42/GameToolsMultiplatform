package com.jaehl.gameTool.common.ui.screens.home

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.screens.gameDetails.GameDetailsRobot
import com.jaehl.gameTool.common.ui.screens.userDetails.UserDetailsRobot
import org.hamcrest.CoreMatchers
import org.junit.Assert


class HomeRobot(
    private val device: UiDevice,
    private val timeout : Long
) {

    private val navTitle = "Home"

    fun assertTitleHome() = apply {
        val textView = device.findObject(By.res( TestTags.General.nav_title))
        Assert.assertThat(textView.getText(), CoreMatchers.`is`(CoreMatchers.equalTo(navTitle)))
    }

    fun isHomePage() : Boolean {
        val textView = device.findObject(By.res( "navTitle")) ?: return false
        return (textView.getText() == navTitle)
    }

    fun clickNavAccountDetailsAndTransition() : UserDetailsRobot {
        device.findObject(By.res( TestTags.Home.nav_account_details))
            .clickAndWait(Until.newWindow(), timeout)
        return UserDetailsRobot(device, timeout)
    }

    fun waitUnitLoadingFinished() = apply {
        device.wait(Until.gone(By.res( TestTags.General.loading_indicator)), timeout)
    }

    fun clickGameRowAndTransition(rowIndex : Int) : GameDetailsRobot {
        device.wait(Until.hasObject(By.res( TestTags.Home.game_row)), timeout)
        device.findObjects(By.res( TestTags.Home.game_row))[rowIndex].clickAndWait(Until.newWindow(), timeout)
        return GameDetailsRobot(device, timeout)
    }
}