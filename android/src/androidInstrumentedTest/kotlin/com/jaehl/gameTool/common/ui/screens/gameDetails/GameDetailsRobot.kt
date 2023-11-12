package com.jaehl.gameTool.common.ui.screens.gameDetails

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.jaehl.gameTool.common.ui.TestTags
import com.jaehl.gameTool.common.ui.screens.collectionList.CollectionListRobot
import com.jaehl.gameTool.common.ui.screens.itemList.ItemListRobot

class GameDetailsRobot(
    private val device: UiDevice,
    private val timeout : Long
) {
    fun clickItemsButtonAndTransition() : ItemListRobot{
        device.findObject(By.res(TestTags.GameDetails.items_button))
            .clickAndWait(Until.newWindow(), timeout)
        return ItemListRobot(device, timeout)
    }

    fun clickCollectionsButtonAndTransition() : CollectionListRobot {
        device.findObject(By.res( TestTags.GameDetails.collections_button))
            .clickAndWait(Until.newWindow(), timeout)
        return CollectionListRobot(device, timeout)
    }

    fun waitUnitLoadingFinished() = apply {
        device.wait(Until.gone(By.res( TestTags.General.loading_indicator)), timeout)
    }
}