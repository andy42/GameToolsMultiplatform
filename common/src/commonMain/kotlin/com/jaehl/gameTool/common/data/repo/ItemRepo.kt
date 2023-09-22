package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.service.ItemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ItemRepo {
    fun getItems(gameId : Int) : Flow<List<Item>>
    fun getItemFlow(id : Int) : Flow<Item>
    fun getItem(id : Int) : Item?

    fun addItem(
        game : Int,
        name : String,
        categories : List<Int>,
        image : Int
    ) : Item
    fun updateItem(
        game: Int,
        name : String,
        categories : List<Int>,
        image : Int
    ) : Item

    fun getItemCategories(gameId : Int) : Flow<List<ItemCategory>>
}

//TODO add local caching
class ItemRepoImp(
    private val jobDispatcher: JobDispatcher,
    private val itemService: ItemService
) : ItemRepo {

    private val itemsMap = hashMapOf<Int, Item>()
    private val itemCategoriesMap = hashMapOf<Int, ItemCategory>()

    override fun getItems(gameId : Int) = flow {
        val items = itemService.getItems(gameId)
        itemsMap.clear()
        items.forEach {
            itemsMap[it.id] = it
        }
        emit(items)
    }

    override fun getItem(id: Int): Item? {
        val item = itemsMap[id]
        if(item == null){
            val newItem = itemService.getItem(id)
            itemsMap[newItem.id] = newItem
            return newItem
        } else {
            return item
        }
    }

    override fun getItemFlow(id: Int) = flow {
        emit(itemService.getItem(id))
    }

    override fun addItem(
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

    override fun updateItem(
        game: Int,
        name : String,
        categories : List<Int>,
        image : Int
    ): Item {
        val item = itemService.updateItem(
            game = game,
            name = name,
            categories = categories,
            image = image
        )
        itemsMap[item.id] = item
        return item
    }

    override fun getItemCategories(gameId: Int) = flow {
        emit(itemCategoriesMap.values.toList())

        val itemCategories = itemService.getItemCategories()
        itemCategoriesMap.clear()
        itemCategories.forEach {
            itemCategoriesMap[it.id] = it
        }
        emit(itemCategoriesMap.values.toList())
    }
}