package com.jaehl.gameTool.common.data.local

import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.ui.util.UiException

interface GameLocalSource {
    suspend fun getGames() : List<Game>
    suspend fun getGame(id : Int) : Game

    suspend fun addUpdateGame(game : Game)
    suspend fun addUpdateGames(games : List<Game>)

    suspend fun deleteGame(gameId : Int)
}

class GameLocalSourceInMemory() : GameLocalSource {

    private val gamesMap : HashMap<Int, Game> = HashMap()

    override suspend fun getGames(): List<Game> {
        return gamesMap.values.toList()
    }

    override suspend fun getGame(id: Int): Game {
        return gamesMap[id] ?: throw UiException.NotFound("Game not found")
    }

    override suspend fun addUpdateGame(game: Game) {
        gamesMap[game.id] = game
    }

    override suspend fun addUpdateGames(games: List<Game>) {
        gamesMap.clear()
        games.forEach { game ->
            gamesMap[game.id] = game
        }
    }

    override suspend fun deleteGame(gameId: Int) {
        gamesMap.remove(gameId)
    }
}