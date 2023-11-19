package com.jaehl.gameTool.common.ui.screens.collectionDetails

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.TestTags

class CollectionDetailsRobot(
    private val device: UiDevice,
    private val timeout : Long
) {
    fun waitUnitLoadingFinished() = apply {
        device.wait(Until.gone(By.res( TestTags.General.loading_indicator)), timeout)
    }
}