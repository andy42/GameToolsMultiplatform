package com.jaehl.gameTool.apiClientKtor.data.service

import com.jaehl.gameTool.apiClientKtor.data.model.AddCollectionGroupRequest
import com.jaehl.gameTool.apiClientKtor.data.model.AddCollectionItemAmountRequest
import com.jaehl.gameTool.apiClientKtor.data.model.UpdateGroupPreferencesRequest
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewAdminCollectionRequest
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest
import com.jaehl.gameTool.common.data.service.CollectionService
import io.ktor.http.*

class CollectionServiceKtor(
    private val requestUtil : RequestUtil
) : CollectionService {
    override suspend fun getCollections(): List<Collection> {
        return requestUtil.createRequest(
            url = "collections",
            HttpMethod.Get
        )
    }

    override suspend fun getCollections(gameId: Int?): List<Collection> {
        return requestUtil.createRequest(
            url = "collections${if(gameId == null) "" else "?gameId=$gameId"}",
            HttpMethod.Get
        )
    }

    override suspend fun getCollection(collectionId: Int): Collection {
        return requestUtil.createRequest(
            url = "collections/$collectionId",
            HttpMethod.Get
        )
    }

    override suspend fun addCollection(data: NewCollectionRequest): Collection {
        return requestUtil.createRequest(
            url = "collections/new",
            HttpMethod.Post,
            requestBody = data
        )
    }

    override suspend fun addAdminCollection(data: NewAdminCollectionRequest): Collection {
        return requestUtil.createRequest(
            url = "admin/collections/New",
            HttpMethod.Post,
            requestBody = data
        )
    }

    override suspend fun deleteCollection(collectionId: Int) {
        requestUtil.createRequestNoResponse(
            url = "collections/$collectionId",
            HttpMethod.Delete
        )
    }

    override suspend fun updateCollection(collectionId: Int, data: UpdateCollectionRequest): Collection {
        return requestUtil.createRequest(
            url = "collections/$collectionId",
            HttpMethod.Post,
            requestBody = data
        )
    }

    override suspend fun addGroup(collectionId: Int): Collection.Group {
        return requestUtil.createRequest(
            url = "collections/$collectionId/new",
            HttpMethod.Post,
            requestBody = AddCollectionGroupRequest(name = "")
        )
    }

    override suspend fun deleteGroup(collectionId: Int, groupId: Int) {
        requestUtil.createRequestNoResponse(
            url = "collections/$collectionId/$groupId",
            HttpMethod.Delete
        )
    }

    override suspend fun addUpdateItemAmount(
        collectionId: Int,
        groupId: Int,
        itemId: Int,
        amount: Int
    ): Collection.ItemAmount {
        return requestUtil.createRequest(
            url = "collections/$collectionId/$groupId/$itemId",
            HttpMethod.Post,
            requestBody = AddCollectionItemAmountRequest(amount = amount)
        )
    }

    override suspend fun deleteItemAmount(collectionId: Int, groupId: Int, itemId: Int) {
        requestUtil.createRequestNoResponse(
            url = "collections/$collectionId/$groupId/$itemId",
            HttpMethod.Delete
        )
    }

    override suspend fun updateGroupPreferences(
        collectionId: Int,
        groupId: Int,
        showBaseIngredients: Boolean,
        collapseIngredients: Boolean,
        costReduction: Float,
        itemRecipePreferenceMap: Map<Int, Int?>
    ): Collection.Group {
        return requestUtil.createRequest(
            url = "collections/$collectionId/$groupId/preferences",
            HttpMethod.Post,
            requestBody = UpdateGroupPreferencesRequest(
                showBaseIngredients = showBaseIngredients,
                collapseIngredients = collapseIngredients,
                costReduction = costReduction,
                itemRecipePreferenceMap = itemRecipePreferenceMap
            )
        )
    }
}