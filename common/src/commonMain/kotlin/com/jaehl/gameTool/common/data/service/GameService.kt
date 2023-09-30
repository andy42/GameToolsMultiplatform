package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.Game

interface GameService {
    fun createGame(name : String, icon : Int, banner : Int) : Game
    fun updateGame(id : Int, name : String, icon : Int, banner : Int) : Game
    fun deleteGame(id : Int)
    fun getGame(id : Int) : Game
    fun getGames() : List<Game>
}