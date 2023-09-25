package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.baseBody
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.CreateGameRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.UpdateGameRequest
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.service.GameService

class GameServiceRetroFit(
    val serverApi : ServerApi,
    val authProvider: AuthProvider
) : GameService {

    override fun createGame(name: String) : Game{
        return serverApi.createGame(
            bearerToken = authProvider.getBearerToken(),
            data = CreateGameRequest(
                name = name
            )
        ).baseBody()
    }

    override fun updateGame(id: Int, name: String) : Game{
        return serverApi.updateGame(
            bearerToken = authProvider.getBearerToken(),
            id = id,
            data = UpdateGameRequest(
                name = name
            )
        ).baseBody()
    }

    override fun deleteGame(id: Int) {
        serverApi.deleteGame(
            bearerToken = authProvider.getBearerToken(),
            id = id
        ).execute()
    }

    override fun getGame(id: Int): Game {
        return serverApi.getGame(
            bearerToken = authProvider.getBearerToken(),
            id = id
        ).baseBody()
    }

    override fun getGames(): List<Game> {
        return serverApi.getGames(
            bearerToken = authProvider.getBearerToken()
        ).baseBody()
    }
}