package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.FlowResource
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.local.ItemLocalSource
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.service.ItemService
import kotlinx.coroutines.flow.flow

interface ItemRepo {
    suspend fun getItems(gameId : Int? = null) : FlowResource<List<Item>>
    suspend fun getItem(id : Int) : FlowResource<Item>
    suspend fun getItemCached(id : Int) : Item?

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

    suspend fun getItemCategories() : FlowResource<List<ItemCategory>>
    suspend fun addItemCategory(name : String) : ItemCategory
}

class ItemRepoImp(
    private val jobDispatcher: JobDispatcher,
    private val itemService: ItemService,
    private val itemLocalSource : ItemLocalSource,
) : ItemRepo {

    override suspend fun getItems(gameId : Int?) = flow {
        try {
            emit(Resource.Loading(itemLocalSource.getItems(gameId)))
            val items = itemService.getItems(gameId)
            itemLocalSource.updateItems(gameId, items)
            emit(Resource.Success(itemLocalSource.getItems(gameId)))
        }
        catch (t :Throwable){
            emit(Resource.Error(t))
        }
    }

    override suspend fun getItemCached(id: Int): Item? {
        val item = itemLocalSource.getItem(id)
        return if(item == null){
            val newItem = itemService.getItem(id)
            itemLocalSource.updateItem(newItem)
            newItem
        } else {
            item
        }
    }

    override suspend fun getItem(id: Int) = flow {
        try {
            emit(Resource.Loading(itemLocalSource.getItem(id)))
            val item = itemService.getItem(id)
            itemLocalSource.updateItem(item)
            emit(Resource.Success(itemLocalSource.getItem(id)))
        }
        catch (t : Throwable){
            emit(Resource.Error(t))
        }
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
        itemLocalSource.updateItem(item)
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
        itemLocalSource.updateItem(item)
        return item
    }

    override suspend fun getItemCategories() = flow {
        try {
            emit(Resource.Loading(itemLocalSource.getItemCategories()))
            val itemCategories = itemService.getItemCategories()
            itemLocalSource.addItemCategories(itemCategories)
            emit(Resource.Success(itemCategories))
        }
        catch (t :Throwable){
            emit(Resource.Error(t))
        }
    }

    override suspend fun addItemCategory(name: String): ItemCategory {
        val itemCategory = itemService.addItemCategories(name)
        itemLocalSource.addItemCategory(itemCategory)
        return itemCategory
    }
}