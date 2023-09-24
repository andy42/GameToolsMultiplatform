package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory

interface ItemService {
    fun getItem(id : Int) : Item
    fun getItems(gameId : Int) : List<Item>
    fun addItem(
        game : Int,
        name : String,
        categories : List<Int>,
        image : Int
    ) : Item

    fun updateItem(
        itemId : Int,
        game: Int,
        name : String,
        categories : List<Int>,
        image : Int
    ) : Item

    fun getItemCategories() : List<ItemCategory>
    fun addItemCategories(name : String) : ItemCategory
}