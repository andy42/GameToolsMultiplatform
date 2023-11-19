package com.jaehl.gameTool.common.data

import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.model.ItemCategory

object GameMockData {
    fun createGame(
        id : Int,
        name : String = "test Game $id",
        itemCategories : List<ItemCategory> = listOf(),
        icon : Int = 1,
        banner : Int = 2
    ) : Game {
        return Game(
            id = id,
            name = name,
            itemCategories = itemCategories,
            icon = icon,
            banner = banner
        )
    }
}