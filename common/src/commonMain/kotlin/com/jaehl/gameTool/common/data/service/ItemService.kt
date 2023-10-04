package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory

interface ItemService {
    suspend fun getItem(id : Int) : Item
    suspend fun getItems(gameId : Int) : List<Item>
    suspend fun getItems() : List<Item>
    suspend fun addItem(
        game : Int,
        name : String,
        categories : List<Int>,
        image : Int
    ) : Item

    suspend fun updateItem(
        itemId : Int,
        game: Int,
        name : String,
        categories : List<Int>,
        image : Int
    ) : Item

    suspend fun getItemCategories() : List<ItemCategory>
    suspend fun addItemCategories(name : String) : ItemCategory
}