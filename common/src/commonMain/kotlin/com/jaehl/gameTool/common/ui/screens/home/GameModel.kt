package com.jaehl.gameTool.common.ui.screens.home

import com.jaehl.gameTool.common.data.model.Game

data class GameModel(
    val id : Int,
    val name : String
) {
    companion object {
        fun create(game : Game) : GameModel{
            return GameModel (
                id = game.id,
                name = game.name
            )
        }
    }
}
