package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewAdminCollectionRequest
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest


interface CollectionService {
    suspend fun getCollections() : List<Collection>
    suspend fun getCollections(gameId : Int) : List<Collection>
    suspend fun getCollection(collectionId : Int) : Collection

    suspend fun addCollection(data : NewCollectionRequest) : Collection
    suspend fun addAdminCollection(data : NewAdminCollectionRequest) : Collection
    suspend fun deleteCollection(collectionId : Int)
    suspend fun updateCollection(collectionId : Int, data : UpdateCollectionRequest) : Collection

    suspend fun addGroup(collectionId : Int) : Collection.Group
    suspend fun deleteGroup(collectionId: Int, groupId: Int)

    suspend fun addUpdateItemAmount(collectionId: Int, groupId: Int, itemId : Int, amount : Int) : Collection.ItemAmount
    suspend fun deleteItemAmount(collectionId: Int, groupId: Int, itemId : Int)
}