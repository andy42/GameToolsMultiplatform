package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.FlowResource
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.util.UiException
import kotlinx.coroutines.flow.flow


class GameRepoMock : GameRepo {

    var gameList : ArrayList<Game> = arrayListOf()
    val itemCategoryList : ArrayList<ItemCategory> = arrayListOf()

    var gameListResourceError : Resource.Error? = null
    var gameResourceError : Resource.Error? = null
    var itemCategoryListError : Resource.Error? = null

    override suspend fun getGames(): FlowResource<List<Game>> = flow {
        gameListResourceError?.let {
            emit(it)
            return@flow
        }
        emit(Resource.Success(gameList))
    }

    override suspend fun getGameFlow(id: Int): FlowResource<Game> = flow {

        gameResourceError?.let {
            emit(it)
            return@flow
        }

        val game = gameList.firstOrNull{it.id == id}
        if(game == null){
            emit(Resource.Error(UiException.NotFound("game id not found")))
        } else {
            Resource.Success(game)
        }
    }

    override suspend fun getGameItemCategories(gameId: Int): FlowResource<List<ItemCategory>> = flow {
        itemCategoryListError?.let {
            emit(it)
            return@flow
        }
        emit(Resource.Success(itemCategoryList))
    }

    private fun createUniqueGameId() : Int = (gameList.lastOrNull()?.id  ?: 0) + 1

    override suspend fun createGame(name: String, itemCategories: List<Int>, icon: Int, banner: Int): Game {

        val itemCategoryMap : HashMap<Int, ItemCategory> = hashMapOf()
        itemCategoryList.forEach {
            itemCategoryMap[it.id] = it
        }

        val game = Game(
            id = createUniqueGameId(),
            name = name,
            itemCategories = itemCategories.mapNotNull {
                itemCategoryMap[it]
            },
            icon = icon,
            banner = banner
        )
        gameList.add(game)
        return game
    }

    override suspend fun updateGame(id: Int, name: String, itemCategories: List<Int>, icon: Int, banner: Int): Game {
        val itemCategoryMap : HashMap<Int, ItemCategory> = hashMapOf()
        itemCategoryList.forEach {
            itemCategoryMap[it.id] = it
        }

        val gameIndex = gameList.indexOfFirst { it.id == id }
        if(gameIndex == -1) throw UiException.NotFound("game not found")
        val game = gameList[gameIndex].copy(
            name = name,
            itemCategories = itemCategories.mapNotNull {
                itemCategoryMap[it]
            },
            icon = icon,
            banner = banner
        )
        gameList[gameIndex] = game
        return game
    }

    override suspend fun delete(id: Int) {
        gameList.removeIf{it.id == id}
    }

    fun clear() {
        gameList.clear()
        itemCategoryList.clear()

        gameListResourceError = null
        gameResourceError = null
        itemCategoryListError = null
    }
}