package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.service.ItemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ItemRepo {
    fun getItems(gameId : Int) : Flow<List<Item>>
    fun getItemFlow(id : Int) : Flow<Item>
    fun getItem(id : Int) : Item?
}

//TODO add local caching
class ItemRepoImp(
    private val jobDispatcher: JobDispatcher,
    private val itemService: ItemService
) : ItemRepo {

    private val itemsMap = hashMapOf<Int, Item>()

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
}