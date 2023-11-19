package com.jaehl.gameTool.apiClientKtor.data.service

import com.jaehl.gameTool.apiClientKtor.data.model.AddItemCategoriesRequest
import com.jaehl.gameTool.apiClientKtor.data.model.AddItemRequest
import com.jaehl.gameTool.apiClientKtor.data.model.UpdateItemRequest
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.service.ItemService
import io.ktor.http.*

class ItemServiceKtor(
    private val requestUtil : RequestUtil
) : ItemService {
    override suspend fun getItem(id: Int): Item {
        return requestUtil.createRequest(
            url = "items/$id",
            HttpMethod.Get
        )
    }

    override suspend fun getItems(gameId: Int?): List<Item> {
        return requestUtil.createRequest(
            url = "items${if(gameId == null) "" else "?gameId=$gameId"}",
            HttpMethod.Get
        )
    }

    override suspend fun addItem(game: Int, name: String, categories: List<Int>, image: Int): Item {
        return requestUtil.createRequest(
            url = "items/new",
            HttpMethod.Post,
            requestBody = AddItemRequest(
                game = game,
                name = name,
                categories = categories,
                image = image
            )
        )
    }

    override suspend fun updateItem(itemId: Int, game: Int, name: String, categories: List<Int>, image: Int): Item {
        return requestUtil.createRequest(
            url = "items/$itemId",
            HttpMethod.Post,
            requestBody = UpdateItemRequest(
                game = game,
                name = name,
                categories = categories,
                image = image
            )
        )
    }

    override suspend fun getItemCategories(): List<ItemCategory> {
        return requestUtil.createRequest(
            url = "items/Categories",
            HttpMethod.Get
        )
    }

    override suspend fun addItemCategories(name: String): ItemCategory {
        return requestUtil.createRequest(
            url = "items/Categories/new",
            HttpMethod.Post,
            requestBody = AddItemCategoriesRequest(name = name)
        )
    }
}