package com.jaehl.gameTool.common.extensions

import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.viewModel.ItemModel

suspend fun Item.toItemModel(appConfig : AppConfig, tokenProvider: TokenProvider) : ItemModel {
    return ItemModel(
        id = this.id,
        name = this.name,
        iconPath = ImageResource.ImageApiResource(
            url = "${appConfig.baseUrl}/images/${this.image}",
            authHeader = tokenProvider.getBearerRefreshToken()
        ),
        categories = this.categories
    )
}