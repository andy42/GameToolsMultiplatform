package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest
import com.jaehl.gameTool.common.data.service.CollectionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CollectionRepo {
    fun getCollections(gameId : Int) : Flow<List<Collection>>
    fun getCollectionFlow(collectionId : Int) : Flow<Collection>
    fun getCollection(collectionId : Int) : Collection
    fun updateCollection(collectionId: Int, body : UpdateCollectionRequest) : Collection

    fun addCollection(data : NewCollectionRequest) : Collection
    fun deleteCollection(collectionId : Int)

    fun addGroup(collectionId : Int) : Collection.Group
    fun deleteGroup(collectionId : Int, groupId : Int)

    fun addUpdateItemAmount(collectionId : Int, groupId : Int, itemId : Int, amount : Int) : Collection.ItemAmount
    fun deleteItemAmount(collectionId : Int, groupId : Int, itemId : Int)
}

class CollectionRepoImp(
    private val jobDispatcher: JobDispatcher,
    private val collectionService : CollectionService
) : CollectionRepo {

    private val collectionMap = LinkedHashMap<Int, Collection>()
    private var loadedGameId : Int = -1

    override fun getCollections(gameId : Int) = flow {

        if(loadedGameId ==gameId ) {
            emit(collectionMap.values.toList())
        }

        collectionMap.clear()
        val collections = collectionService.getCollections(gameId)
        collections.forEach { collection ->
            collectionMap[collection.id] = collection
        }

       emit(collections)
    }

    override fun getCollectionFlow(collectionId: Int): Flow<Collection> = flow {
        collectionMap[collectionId]?.let {
            emit(it)
        }
        val collection = collectionService.getCollection(collectionId)
        collectionMap[collection.id] = collection

        emit(collection)
    }

    override fun getCollection(collectionId: Int): Collection {
        val collection = collectionService.getCollection(collectionId)
        collectionMap[collection.id] = collection
        return collection
    }

    override fun deleteCollection(collectionId: Int) {
        collectionService.deleteCollection(collectionId)
        collectionMap.remove(collectionId)
    }

    override fun updateCollection(collectionId: Int, body: UpdateCollectionRequest) : Collection {
        val collection = collectionService.updateCollection(collectionId, body)
        collectionMap[collection.id] = collection
        return collection
    }

    override fun addCollection(data: NewCollectionRequest): Collection {
        val collection = collectionService.addCollection(data)
        collectionMap[collection.id] = collection
        return collection
    }

    override fun addGroup(collectionId: Int): Collection.Group {
        val group = collectionService.addGroup(collectionId)
        val collection = collectionMap[collectionId] ?: throw Exception("addGroup collection not found : $collectionId")
        val groups = collection.groups.toMutableList()
        groups.add(group)

        collectionMap[collectionId] = collection.copy(
            groups = groups
        )
        return group
    }

    override fun deleteGroup(collectionId : Int, groupId: Int) {
        collectionService.deleteGroup(collectionId, groupId)

        val collection = collectionMap[collectionId] ?: throw Exception("deleteGroup collection not found : $collectionId")
        val groups = collection.groups.toMutableList()
        groups.removeIf{it.id == groupId}

        collectionMap[collectionId] = collection.copy(
            groups = groups
        )
    }

    override fun addUpdateItemAmount(collectionId : Int, groupId: Int, itemId: Int, amount: Int): Collection.ItemAmount {
        val itemAmount = collectionService.addUpdateItemAmount(collectionId, groupId, itemId, amount)

        val collection = collectionMap[collectionId] ?: throw Exception("addUpdateItemAmount collection not found : $collectionId")
        val groups = collection.groups.toMutableList()
        var group = collection.groups.firstOrNull {it.id == groupId} ?: throw Exception("addUpdateItemAmount groupId not found : $groupId")

        if(group.itemAmounts.firstOrNull { it.itemId ==  itemId} == null ){
            group = group.copy(
                itemAmounts = group.itemAmounts.let {
                    val list = it.toMutableList()
                    list.add(itemAmount)
                    return@let list
                }
            )
        } else {

        }
        return itemAmount
    }

    override fun deleteItemAmount(collectionId : Int, groupId: Int, itemId: Int) {
        return collectionService.deleteItemAmount(collectionId, groupId, itemId)
    }
}