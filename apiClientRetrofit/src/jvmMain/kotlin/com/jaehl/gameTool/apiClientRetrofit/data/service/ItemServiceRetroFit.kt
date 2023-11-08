package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddItemCategoriesRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddItemRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.UpdateItemRequest
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.ItemService

class ItemServiceRetroFit(
    val serverApi : ServerApi,
    val tokenProvider : TokenProvider
) : ItemService {

    override suspend fun getItem(id: Int): Item {
        return serverApi.getItem(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = id
        ).data
    }

    override suspend fun getItems(gameId: Int?): List<Item> {
        if(gameId == null) {
            return serverApi.getItems(
                bearerToken = tokenProvider.getBearerAccessToken(),
            ).data
        }
        return serverApi.getItems(
            bearerToken = tokenProvider.getBearerAccessToken(),
            gameId = gameId
        ).data
    }

    override suspend fun addItem(game: Int, name: String, categories: List<Int>, image: Int): Item {
        return serverApi.addItem(
            bearerToken = tokenProvider.getBearerAccessToken(),
            data = AddItemRequest(
                game = game,
                name = name,
                categories = categories,
                image = image
            )
        ).data
    }

    override suspend fun updateItem(itemId : Int, game: Int, name: String, categories: List<Int>, image: Int): Item {
        return serverApi.updateItem(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = itemId,
            data = UpdateItemRequest(
                game = game,
                name = name,
                categories = categories,
                image = image
            )
        ).data
    }

    override suspend fun getItemCategories(): List<ItemCategory> {
        return serverApi.getItemCategories(
            bearerToken = tokenProvider.getBearerAccessToken()
        ).data
    }

    override suspend fun addItemCategories(name: String) : ItemCategory {
        return serverApi.addItemCategories(
            bearerToken = tokenProvider.getBearerAccessToken(),
            data =  AddItemCategoriesRequest(name = name)
        ).data
    }
}