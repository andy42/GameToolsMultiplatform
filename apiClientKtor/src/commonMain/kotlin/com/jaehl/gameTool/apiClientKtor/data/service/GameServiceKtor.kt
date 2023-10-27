package com.jaehl.gameTool.apiClientKtor.data.service

import com.jaehl.gameTool.apiClientKtor.data.model.CreateGameRequest
import com.jaehl.gameTool.apiClientKtor.data.model.UpdateGameRequest
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.service.GameService
import io.ktor.http.*

class GameServiceKtor(
    private val requestUtil : RequestUtil
) : GameService {

    override suspend fun createGame(name: String, itemCategories: List<Int>, icon: Int, banner: Int): Game {

        return requestUtil.createRequest(
            url = "games/new",
            HttpMethod.Post,
            requestBody = CreateGameRequest(
                name = name,
                itemCategories = itemCategories,
                icon = icon,
                banner = banner
            )
        )
    }

    override suspend fun updateGame(
        id: Int,
        name: String,
        itemCategories: List<Int>,
        icon: Int,
        banner: Int): Game {

        return requestUtil.createRequest(
            url = "games/$id",
            HttpMethod.Post,
            requestBody = UpdateGameRequest(
                name = name,
                itemCategories = itemCategories,
                icon = icon,
                banner = banner
            )
        )
    }

    override suspend fun deleteGame(id: Int) {
        requestUtil.createRequestNoResponse(
            url = "games/$id",
            HttpMethod.Delete
        )
    }

    override suspend fun getGame(id: Int): Game {
        return requestUtil.createRequest(
            url = "games/$id",
            HttpMethod.Get
        )
    }

    override suspend fun getGames(): List<Game> {
        return requestUtil.createRequest(
            url = "games",
            HttpMethod.Get
        )
    }
}