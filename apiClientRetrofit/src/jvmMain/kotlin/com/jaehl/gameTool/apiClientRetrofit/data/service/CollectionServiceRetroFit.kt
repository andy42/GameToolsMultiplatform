package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.baseBody
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddCollectionGroupRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddCollectionItemAmountRequest
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest
import com.jaehl.gameTool.common.data.service.CollectionService

class CollectionServiceRetroFit (
    val serverApi : ServerApi,
    val authProvider: AuthProvider
) : CollectionService {

    override fun getCollections(gameId: Int): List<Collection> {
        return serverApi.getCollections(
            bearerToken = authProvider.getBearerToken(),
            gameId = gameId
        ).baseBody()
    }

    override fun getCollection(collectionId: Int): Collection {
        return serverApi.getCollection(
            bearerToken = authProvider.getBearerToken(),
            id = collectionId
        ).baseBody()
    }

    override fun addCollection(data : NewCollectionRequest): Collection {
        return serverApi.addCollection(
            bearerToken = authProvider.getBearerToken(),
            data = data
        ).baseBody()
    }

    override fun deleteCollection(collectionId: Int) {
        serverApi.deleteCollection(
            bearerToken = authProvider.getBearerToken(),
            id = collectionId
        ).execute()
    }

    override fun updateCollection(collectionId: Int, data: UpdateCollectionRequest): Collection {
        return serverApi.updateCollection(
            bearerToken = authProvider.getBearerToken(),
            collectionId = collectionId,
            data = data
        ).baseBody()
    }

    override fun addGroup(collectionId: Int): Collection.Group {
        return serverApi.addCollectionGroup(
            bearerToken = authProvider.getBearerToken(),
            collectionId = collectionId,
            data = AddCollectionGroupRequest(name = "")
        ).baseBody()
    }

    override fun deleteGroup(collectionId: Int, groupId: Int) {
        serverApi.deleteCollectionGroup(
            bearerToken = authProvider.getBearerToken(),
            collectionId = collectionId,
            groupId = groupId
        ).request()
    }

    override fun addUpdateItemAmount(collectionId: Int, groupId: Int, itemId: Int, amount: Int): Collection.ItemAmount {
        return serverApi.addUpdateItemAmount(
            bearerToken = authProvider.getBearerToken(),
            collectionId = collectionId,
            groupId = groupId,
            itemId = itemId,
            data = AddCollectionItemAmountRequest(amount = amount)
        ).baseBody()
    }

    override fun deleteItemAmount(collectionId: Int, groupId: Int, itemId: Int) {
        serverApi.deleteItemAmount(
            bearerToken = authProvider.getBearerToken(),
            collectionId = collectionId,
            groupId = groupId,
            itemId = itemId
        ).request()
    }
}