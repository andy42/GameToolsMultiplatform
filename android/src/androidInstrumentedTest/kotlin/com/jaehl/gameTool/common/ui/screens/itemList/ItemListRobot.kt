package com.jaehl.gameTool.common.ui.screens.itemList

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.screens.itemDetails.ItemDetailsRobot

class ItemListRobot(
    private val device: UiDevice,
    private val timeout : Long
) {

    fun clickItemRowAndTransition(rowIndex : Int) : ItemDetailsRobot{
        device.wait(Until.hasObject(By.res( TestTags.ItemList.item_row)), timeout)
        device.findObject(By.res( TestTags.ItemList.item_row))
            .clickAndWait(Until.newWindow(), timeout)
        return ItemDetailsRobot(device, timeout)
    }

    fun waitUnitLoadingFinished() = apply {
        device.wait(Until.gone(By.res( TestTags.General.loading_indicator)), timeout)
    }
}