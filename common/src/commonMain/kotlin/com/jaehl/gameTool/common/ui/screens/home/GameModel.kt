package com.jaehl.gameTool.common.ui.screens.home

import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.screens.gameDetails.GameDetailsScreenModel

data class GameModel(
    val id : Int = -1,
    val name : String = "",
    val icon : ImageResource = ImageResource.ImageLocalResource(""),
    val banner : ImageResource = ImageResource.ImageLocalResource(""),
)

fun Game.toGameModel(appConfig: AppConfig, authProvider: AuthProvider) : GameModel {
    return GameModel(
        id = this.id,
        name = this.name,
        icon = ImageResource.ImageApiResource(
            url = "${appConfig.baseUrl}/images/${this.icon}",
            authHeader = authProvider.getBearerToken()
        ),
        banner = ImageResource.ImageApiResource(
            url = "${appConfig.baseUrl}/images/${this.banner}",
            authHeader = authProvider.getBearerToken()
        )
    )
}