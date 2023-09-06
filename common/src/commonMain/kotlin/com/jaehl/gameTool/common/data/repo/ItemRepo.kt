package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.service.ItemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

interface ItemRepo {
    fun getItems(gameId : Int) : Flow<List<Item>>
    fun getItem(id : Int) : Flow<Item>
}

//TODO add local caching
class ItemRepoImp(
    private val jobDispatcher: JobDispatcher,
    private val itemService: ItemService
) : ItemRepo {

    override fun getItems(gameId : Int) = flow {
        emit(itemService.getItems(gameId))
    }

    override fun getItem(id: Int) = flow {
        emit(itemService.getItem(id))
    }
}