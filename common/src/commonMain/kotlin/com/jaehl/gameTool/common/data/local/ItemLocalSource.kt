package com.jaehl.gameTool.common.data.local

import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.util.UiException

interface ItemLocalSource {
    suspend fun getItems(gameId : Int) : List<Item>
    suspend fun getItem(itemId : Int) : Item

    suspend fun updateItem(item : Item)
    suspend fun updateItems(gameId : Int, items : List<Item>)

    suspend fun getItemCategories() : List<ItemCategory>
    suspend fun addItemCategory(itemCategory : ItemCategory)
    suspend fun addItemCategories(itemCategories : List<ItemCategory>)
}

class ItemLocalSourceInMemory() : ItemLocalSource {

    private val itemsMap = hashMapOf<Int, Item>()
    private var itemCategories = arrayListOf<ItemCategory>()

    override suspend fun getItems(gameId: Int): List<Item> {
        return itemsMap.values.filter { it.game == gameId }
    }

    override suspend fun getItem(itemId: Int): Item {
        return itemsMap[itemId] ?: throw UiException.NotFound("item not found : $itemId")
    }

    override suspend fun updateItem(item: Item) {
        itemsMap[item.id] = item
    }

    override suspend fun updateItems(gameId: Int, items: List<Item>) {
        itemsMap.entries.removeIf{
            it.value.game == gameId
        }
        items.forEach {
            itemsMap[it.id] = it
        }
    }

    override suspend fun getItemCategories(): List<ItemCategory> {
        return itemCategories
    }

    override suspend fun addItemCategory(itemCategory: ItemCategory) {
        itemCategories.add(itemCategory)
    }

    override suspend fun addItemCategories(itemCategories: List<ItemCategory>) {
        this.itemCategories.clear()
        this.itemCategories.addAll(itemCategories)
    }
}