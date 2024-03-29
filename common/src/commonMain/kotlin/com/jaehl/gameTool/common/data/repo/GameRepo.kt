package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.FlowResource
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.local.GameLocalSource
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.service.GameService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface GameRepo {
    suspend fun getGames() : FlowResource<List<Game>>
    suspend fun getGameFlow(id : Int) : FlowResource<Game>
    suspend fun getGameItemCategories(gameId : Int) : FlowResource<List<ItemCategory>>

    suspend fun createGame(name : String, itemCategories : List<Int>, icon : Int, banner : Int) : Game
    suspend fun updateGame(id : Int, name : String, itemCategories : List<Int>, icon : Int, banner : Int) : Game
    suspend fun delete(id : Int)
}

class GameRepoImp(
    private val gameService: GameService,
    private val gameLocalSource : GameLocalSource
) : GameRepo {

    override suspend fun getGames(): Flow<Resource<List<Game>>> = flow {

        try {
            emit(
                Resource.Loading(gameLocalSource.getGames())
            )

            val games = gameService.getGames()
            gameLocalSource.addUpdateGames(games)
            emit(
                Resource.Success(gameLocalSource.getGames())
            )
        }
        catch (t : Throwable) {
            emit(Resource.Error(t))
        }
    }

    override suspend fun getGameItemCategories(gameId: Int): FlowResource<List<ItemCategory>> = flow {
        emit(
            Resource.Loading(gameLocalSource.getGame(gameId).itemCategories)
        )
        try {
            val game = gameService.getGame(gameId)
            gameLocalSource.addUpdateGame(game)
            emit(Resource.Success(game.itemCategories))
        }
        catch (t : Throwable) {
            emit(Resource.Error(t))
        }
    }

    override suspend fun getGameFlow(id: Int): FlowResource<Game> = flow{
        try {
            emit(Resource.Loading(gameLocalSource.getGame(id)))
            val game = gameService.getGame(id)
            gameLocalSource.addUpdateGame(game)
            emit(Resource.Success(game))
        }
        catch (t : Throwable) {
            emit(Resource.Error(t))
        }
    }

    override suspend fun createGame(name: String, itemCategories : List<Int>, icon : Int, banner : Int): Game {
        val game = gameService.createGame(name, itemCategories, icon, banner)
        gameLocalSource.addUpdateGame(game)
        return game
    }

    override suspend fun updateGame(id: Int, name: String, itemCategories : List<Int>, icon : Int, banner : Int): Game {
        val game = gameService.updateGame(id, name, itemCategories, icon, banner)
        gameLocalSource.addUpdateGame(game)
        return game
    }

    override suspend fun delete(id: Int) {
        gameService.deleteGame(id)
        gameLocalSource.deleteGame(id)
    }
}