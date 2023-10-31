package com.jaehl.gameTool.common.data.local

import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.ui.util.UiException

interface CollectionLocalSource {
    suspend fun getCollections(gameId : Int) : List<Collection>
    suspend fun getCollection(collectionId : Int) : Collection

    suspend fun deleteCollection(collectionId: Int)

    suspend fun updateCollection(collection : Collection)
    suspend fun updateCollections(gameId : Int, collections : List<Collection>)
}

class CollectionLocalSourceInMemory() : CollectionLocalSource {

    private val collectionsMap : HashMap<Int, Collection> = HashMap()

    override suspend fun getCollections(gameId: Int): List<Collection> {
        return collectionsMap.values.filter { it.gameId == gameId }
    }

    override suspend fun getCollection(collectionId: Int): Collection {
        return collectionsMap[collectionId] ?: throw UiException.NotFound("collection not found : $collectionId")
    }

    override suspend fun deleteCollection(collectionId: Int) {
        collectionsMap.remove(collectionId)
    }

    override suspend fun updateCollection(collection: Collection) {
        collectionsMap[collection.id] = collection
    }

    override suspend fun updateCollections(gameId : Int, collections: List<Collection>) {
        collectionsMap.entries.removeIf{
            it.value.gameId == gameId
        }
        collections.forEach {
            collectionsMap[it.id] = it
        }
    }
}