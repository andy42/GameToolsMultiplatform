package com.jaehl.gameTool.common.ui.screens.home

import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.ui.componets.ImageResource

data class GameModel(
    val id : Int = -1,
    val name : String = "",
    val icon : ImageResource = ImageResource.ImageLocalResource(""),
    val banner : ImageResource = ImageResource.ImageLocalResource(""),
)

suspend fun Game.toGameModel(appConfig: AppConfig, tokenProvider: TokenProvider) : GameModel {
    return GameModel(
        id = this.id,
        name = this.name,
        icon = ImageResource.ImageApiResource(
            url = "${appConfig.baseUrl}/images/${this.icon}",
            authHeader = tokenProvider.getBearerRefreshToken()
        ),
        banner = ImageResource.ImageApiResource(
            url = "${appConfig.baseUrl}/images/${this.banner}",
            authHeader = tokenProvider.getBearerRefreshToken()
        )
    )
}