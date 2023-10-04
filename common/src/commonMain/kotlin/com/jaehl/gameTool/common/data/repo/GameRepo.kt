package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.service.GameService

interface GameRepo {
    suspend fun getGames() : List<Game>
    suspend fun getGame(id : Int) : Game

    suspend fun createGame(name : String, icon : Int, banner : Int) : Game
    suspend fun updateGame(id : Int, name : String, icon : Int, banner : Int) : Game
    suspend fun delete(id : Int)
}

class GameRepoImp(
    val gameService: GameService
) : GameRepo {

    override suspend fun getGames(): List<Game> {
        return gameService.getGames()
    }

    override suspend fun getGame(id: Int): Game {
        return gameService.getGame(id)
    }

    override suspend fun createGame(name: String, icon : Int, banner : Int): Game {
        return gameService.createGame(name, icon, banner)
    }

    override suspend fun updateGame(id: Int, name: String, icon : Int, banner : Int): Game {
        return gameService.updateGame(id, name, icon, banner)
    }

    override suspend fun delete(id: Int) {
        gameService.deleteGame(id)
    }
}