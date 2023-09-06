package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.service.GameService

interface GameRepo {
    fun getGames() : List<Game>
    fun getGame(id : Int) : Game
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
}