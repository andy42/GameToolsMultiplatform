package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.service.GameService

interface GameRepo {
    fun getGames() : List<Game>
    fun getGame(id : Int) : Game

    fun createGame(name : String, icon : Int, banner : Int) : Game
    fun updateGame(id : Int, name : String, icon : Int, banner : Int) : Game
    fun delete(id : Int)
}

class GameRepoImp(
    val gameService: GameService
) : GameRepo {

    override fun getGames(): List<Game> {
        return gameService.getGames()
    }

    override fun getGame(id: Int): Game {
        return gameService.getGame(id)
    }

    override fun createGame(name: String, icon : Int, banner : Int): Game {
        return gameService.createGame(name, icon, banner)
    }

    override fun updateGame(id: Int, name: String, icon : Int, banner : Int): Game {
        return gameService.updateGame(id, name, icon, banner)
    }

    override fun delete(id: Int) {
        gameService.deleteGame(id)
    }
}