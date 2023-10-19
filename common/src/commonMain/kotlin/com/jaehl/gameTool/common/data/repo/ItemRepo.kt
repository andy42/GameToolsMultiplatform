package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.service.ItemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ItemRepo {
    suspend fun getItems(gameId : Int) : List<Item>
    suspend fun getItemsFlow(gameId : Int) : Flow<List<Item>>
    suspend fun getItemFlow(id : Int) : Flow<Item>
    suspend fun getItem(id : Int) : Item?

    suspend fun preloadItems(gameId : Int)

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

    suspend fun getItemCategories(gameId : Int?) : Flow<List<ItemCategory>>
    suspend fun addItemCategory(name : String) : ItemCategory
}

//TODO add local caching
class ItemRepoImp(
    private val jobDispatcher: JobDispatcher,
    private val itemService: ItemService
) : ItemRepo {

    private val itemsMap = hashMapOf<Int, Item>()
    private val itemCategoriesMap = hashMapOf<Int, ItemCategory>()

    override suspend fun getItemsFlow(gameId : Int) = flow {
        val items = itemService.getItems(gameId)
        itemsMap.clear()
        items.forEach {
            itemsMap[it.id] = it
        }
        emit(items)
    }

    override suspend fun getItems(gameId: Int): List<Item> {
        return itemsMap.values.toList()
    }

    override suspend fun preloadItems(gameId: Int) {
        val items = itemService.getItems(gameId)
        itemsMap.clear()
        items.forEach {
            itemsMap[it.id] = it
        }
    }

    override suspend fun getItem(id: Int): Item? {
        val item = itemsMap[id]
        if(item == null){
            val newItem = itemService.getItem(id)
            itemsMap[newItem.id] = newItem
            return newItem
        } else {
            return item
        }
    }

    override suspend fun getItemFlow(id: Int) = flow {
        emit(itemService.getItem(id))
    }

    override suspend fun addItem(
        game : Int,
        name : String,
        categories : List<Int>,
        image : Int
    ): Item {
        val item = itemService.addItem(
            game = game,
            name = name,
            categories = categories,
            image = image
        )
        itemsMap[item.id] = item
        return item
    }

    override suspend fun updateItem(
        itemId : Int,
        game: Int,
        name : String,
        categories : List<Int>,
        image : Int
    ): Item {
        val item = itemService.updateItem(
            itemId = itemId,
            game = game,
            name = name,
            categories = categories,
            image = image
        )
        itemsMap[item.id] = item
        return item
    }

    override suspend fun getItemCategories(gameId: Int?) = flow {
        emit(itemCategoriesMap.values.toList())

        val itemCategories = itemService.getItemCategories()
        itemCategoriesMap.clear()
        itemCategories.forEach {
            itemCategoriesMap[it.id] = it
        }
        emit(itemCategoriesMap.values.toList())
    }

    override suspend fun addItemCategory(name: String): ItemCategory {
        val itemCategory = itemService.addItemCategories(name)
        itemCategoriesMap[itemCategory.id] = itemCategory
        return itemCategory
    }
}