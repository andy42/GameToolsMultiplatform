package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.FlowResource
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.util.UiException
import kotlinx.coroutines.flow.flow

class ItemRepoMock(
    private val itemCategoriesMap : HashMap<Int, ItemCategory>
) : ItemRepo {

    val itemList = arrayListOf<Item>()

    var getItemListError : Resource.Error? = null
    var getItemError : Resource.Error? = null

    override suspend fun getItems(gameId: Int?): FlowResource<List<Item>> = flow {
        getItemListError?.let {
            emit(it)
            return@flow
        }
        emit(Resource.Success(itemList))
    }

    override suspend fun getItem(id: Int): FlowResource<Item> = flow {
        getItemError?.let {
            emit(it)
            return@flow
        }
        val item = itemList.firstOrNull{it.id == id}
        if(item == null){
            emit(Resource.Error(UiException.NotFound("item id not found : $id")))
            return@flow
        }
        emit(Resource.Success(item))
    }

    override suspend fun getItemCached(id: Int): Item? {
       return itemList.firstOrNull{it.id == id}
    }

    private fun createUniqueItemId() : Int = (itemList.lastOrNull()?.id  ?: 0) + 1

    override suspend fun addItem(game: Int, name: String, categories: List<Int>, image: Int): Item {
        val item = Item(
            id = createUniqueItemId(),
            game = game,
            name = name,
            categories = categories.mapNotNull { itemCategoriesMap[it] },
            image = image
        )
        itemList.add(item)
        return item
    }

    override suspend fun updateItem(itemId: Int, game: Int, name: String, categories: List<Int>, image: Int): Item {
        val itemIndex = itemList.indexOfFirst { it.id == itemId }
        if(itemIndex == -1){
            throw UiException.NotFound("item not found : $itemIndex")
        }
        val item = itemList[itemIndex].copy(
            game = game,
            name = name,
            categories = categories.mapNotNull { itemCategoriesMap[it] },
            image = image
        )
        itemList[itemIndex] = item
        return item
    }

    override suspend fun getItemCategories(): FlowResource<List<ItemCategory>> = flow {
        emit(Resource.Success(itemCategoriesMap.values.toList()))
    }

    private fun createUniqueItemCategoryId() : Int {
        var id = 0
        itemCategoriesMap.values.forEach {
            if(it.id > id) id = it.id
        }
        return id++
    }

    override suspend fun addItemCategory(name: String): ItemCategory {
        val itemCategory = ItemCategory(
            id = createUniqueItemCategoryId(),
            name = name
        )
        itemCategoriesMap[itemCategory.id] = itemCategory
        return itemCategory
    }
}