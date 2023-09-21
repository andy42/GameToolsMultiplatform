package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest


interface CollectionService {
    fun getCollections(gameId : Int) : List<Collection>
    fun getCollection(collectionId : Int) : Collection

    fun addCollection(data : NewCollectionRequest) : Collection
    fun deleteCollection(collectionId : Int)
    fun updateCollection(collectionId : Int, data : UpdateCollectionRequest) : Collection

    fun addGroup(collectionId : Int) : Collection.Group
    fun deleteGroup(collectionId: Int, groupId: Int)

    fun addUpdateItemAmount(collectionId: Int, groupId: Int, itemId : Int, amount : Int) : Collection.ItemAmount
    fun deleteItemAmount(collectionId: Int, groupId: Int, itemId : Int)
}