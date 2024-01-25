package com.jaehl.gameTool.localSourceSqlDelight.data.local

import com.jaehl.gameTool.localSourceSqlDelight.Database
import com.jaehl.gameTool.common.data.local.GameLocalSource
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.model.ItemCategory

class GameLocalSourceSqlDelight(
   private val database: Database
) : GameLocalSource {

    private fun getGameCategories(gameId : Int) : List<ItemCategory> {
        return database.gamesQueries.getGameCategories(gameId).executeAsList().mapNotNull { gameCategory ->
             ItemCategory(
                id = gameCategory.id,
                name = gameCategory.name
            )
        }
    }

    override suspend fun getGames(): List<Game> {
        return database.gamesQueries.selectAll().executeAsList().map {gameEntity ->
            gameEntity.toGame(
                getGameCategories(gameEntity.id)
            )
        }
    }

    override suspend fun getGame(id: Int): Game {
        val gameEntity = database.gamesQueries.getGame(id).executeAsOne()
        return gameEntity.toGame(
            getGameCategories(gameEntity.id)
        )
    }

    override suspend fun addUpdateGame(game: Game) {
        database.gamesQueries.insertGame(game.toGameEntity())
    }

    override suspend fun addUpdateGames(games: List<Game>) {

        val itemCategories = HashSet<ItemCategory>()
        games.forEach { game ->
            game.itemCategories.forEach {
                itemCategories.add(it)
            }
        }

        database.gamesQueries.transaction {
            itemCategories.forEach {
                database.itemsQueries.updateCategories(
                    CategoryEntity(
                        id = it.id,
                        name = it.name
                    )
                )
            }

            games.forEach { game ->
                database.gamesQueries.insertGame(game.toGameEntity())
                database.gamesQueries.deleteGameCategories(game.id)
                game.itemCategories.forEach { itemCategory ->
                    database.gamesQueries.updateGameCategories(
                        GameCategoryEntity(
                            game_id = game.id,
                            category_id = itemCategory.id
                        )
                    )
                }
            }
        }
    }

    override suspend fun deleteGame(gameId: Int) {
        database.gamesQueries.deleteGameCategories(gameId)
        database.gamesQueries.deleteGame(gameId)
    }
}

fun GameEntity.toGame(itemCategories : List<ItemCategory>) : Game {
    return Game(
        id = this.id,
        name = this.name,
        icon = this.icon,
        banner = this.banner,
        itemCategories = itemCategories
    )
}

fun Game.toGameEntity() : GameEntity {
    return GameEntity(
        id = this.id,
        name = this.name,
        icon = this.icon,
        banner = this.banner
    )
}