package com.jaehl.gameTool.common.ui.screens.collectionList

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.screens.collectionDetails.CollectionDetailsRobot

class CollectionListRobot(
    private val device: UiDevice,
    private val timeout : Long
) {
    fun clickCollectionRowAndTransition() : CollectionDetailsRobot{
        device.findObject(By.res( TestTags.ItemList.item_row))
            .clickAndWait(Until.newWindow(), timeout)
        return CollectionDetailsRobot(device, timeout)
    }
}