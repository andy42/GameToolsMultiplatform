package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.Game

interface GameService {
    suspend fun createGame(name : String, icon : Int, banner : Int) : Game
    suspend fun updateGame(id : Int, name : String, icon : Int, banner : Int) : Game
    suspend fun deleteGame(id : Int)
    suspend fun getGame(id : Int) : Game
    suspend fun getGames() : List<Game>
}