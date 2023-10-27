package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.CreateGameRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.UpdateGameRequest
import com.jaehl.gameTool.apiClientRetrofit.data.util.ExceptionHandler
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.GameService

class GameServiceRetroFit(
    val serverApi : ServerApi,
    val tokenProvider : TokenProvider,
    val exceptionHandler : ExceptionHandler
) : GameService {

    override suspend fun createGame(name: String, itemCategories : List<Int>, icon : Int, banner : Int) : Game = exceptionHandler.tryBlock {
        return@tryBlock serverApi.createGame(
            bearerToken = tokenProvider.getBearerAccessToken(),
            data = CreateGameRequest(
                name = name,
                itemCategories = itemCategories,
                icon = icon,
                banner = banner
            )
        ).data
    }

    override suspend fun updateGame(id: Int, name: String, itemCategories : List<Int>, icon : Int, banner : Int) : Game = exceptionHandler.tryBlock {
        return@tryBlock serverApi.updateGame(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = id,
            data = UpdateGameRequest(
                name = name,
                itemCategories = itemCategories,
                icon = icon,
                banner = banner
            )
        ).data
    }

    override suspend fun deleteGame(id: Int) = exceptionHandler.tryBlock {
        serverApi.deleteGame(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = id
        )
    }

    override suspend fun getGame(id: Int): Game = exceptionHandler.tryBlock {
        return@tryBlock serverApi.getGame(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = id
        ).data
    }

    override suspend fun getGames(): List<Game> = exceptionHandler.tryBlock {
        return@tryBlock serverApi.getGames(
            bearerToken = tokenProvider.getBearerAccessToken()
        ).data
    }
}