package com.jaehl.gameTool.common.ui.screens.itemDetails

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.TestTags

class ItemDetailsRobot(
    private val device: UiDevice,
    private val timeout : Long
) {
    fun waitUnitLoadingFinished() = apply {
        device.wait(Until.gone(By.res( TestTags.General.loading_indicator)), timeout)
    }
}