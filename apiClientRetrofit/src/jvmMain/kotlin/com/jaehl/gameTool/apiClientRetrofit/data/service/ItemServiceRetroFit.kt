package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.baseBody
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddItemCategoriesRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddItemRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.UpdateItemRequest
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.service.ItemService

class ItemServiceRetroFit(
    val serverApi : ServerApi,
    val authProvider: AuthProvider
) : ItemService {

    override fun getItem(id: Int): Item {
        return serverApi.getItem(
            bearerToken = authProvider.getBearerToken(),
            id = id
        ).baseBody()
    }

    override fun getItems(gameId: Int): List<Item> {
        return serverApi.getItems(
            bearerToken = authProvider.getBearerToken(),
            gameId = gameId
        ).baseBody()
    }

    override fun addItem(game: Int, name: String, categories: List<Int>, image: Int): Item {
        return serverApi.addItem(
            bearerToken = authProvider.getBearerToken(),
            data = AddItemRequest(
                game = game,
                name = name,
                categories = categories,
                image = image
            )
        ).baseBody()
    }

    override fun updateItem(itemId : Int, game: Int, name: String, categories: List<Int>, image: Int): Item {
        return serverApi.updateItem(
            bearerToken = authProvider.getBearerToken(),
            id = itemId,
            data = UpdateItemRequest(
                game = game,
                name = name,
                categories = categories,
                image = image
            )
        ).baseBody()
    }

    override fun getItemCategories(): List<ItemCategory> {
        return serverApi.getItemCategories(
            bearerToken = authProvider.getBearerToken()
        ).baseBody()
    }

    override fun addItemCategories(name: String) : ItemCategory {
        return serverApi.addItemCategories(
            bearerToken = authProvider.getBearerToken(),
            data =  AddItemCategoriesRequest(name = name)
        ).baseBody()
    }
}