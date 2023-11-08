package com.jaehl.gameTool.apiClientRetrofit.data.service

import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddCollectionGroupRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.AddCollectionItemAmountRequest
import com.jaehl.gameTool.apiClientRetrofit.data.model.request.UpdateGroupPreferencesRequest
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewAdminCollectionRequest
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.CollectionService

class CollectionServiceRetroFit (
    val serverApi : ServerApi,
    val tokenProvider : TokenProvider
) : CollectionService {

    override suspend fun getCollections(): List<Collection> {
        return serverApi.getCollections(
            bearerToken = tokenProvider.getBearerAccessToken()
        ).data
    }

    override suspend fun getCollections(gameId: Int?): List<Collection> {
        if(gameId == null){
            return getCollections()
        }
        return serverApi.getCollections(
            bearerToken = tokenProvider.getBearerAccessToken(),
            gameId = gameId
        ).data
    }

    override suspend fun getCollection(collectionId: Int): Collection {
        return serverApi.getCollection(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = collectionId
        ).data
    }

    override suspend fun addCollection(data : NewCollectionRequest): Collection {
        return serverApi.addCollection(
            bearerToken = tokenProvider.getBearerAccessToken(),
            data = data
        ).data
    }

    override suspend fun addAdminCollection(data: NewAdminCollectionRequest): Collection {
        return serverApi.addAdminCollection(
            bearerToken = tokenProvider.getBearerAccessToken(),
            data = data
        ).data
    }

    override suspend fun deleteCollection(collectionId: Int) {
        serverApi.deleteCollection(
            bearerToken = tokenProvider.getBearerAccessToken(),
            id = collectionId
        )
    }

    override suspend fun updateCollection(collectionId: Int, data: UpdateCollectionRequest): Collection {
        return serverApi.updateCollection(
            bearerToken = tokenProvider.getBearerAccessToken(),
            collectionId = collectionId,
            data = data
        ).data
    }

    override suspend fun addGroup(collectionId: Int): Collection.Group {
        return serverApi.addCollectionGroup(
            bearerToken = tokenProvider.getBearerAccessToken(),
            collectionId = collectionId,
            data = AddCollectionGroupRequest(name = "")
        ).data
    }

    override suspend fun deleteGroup(collectionId: Int, groupId: Int) {
        serverApi.deleteCollectionGroup(
            bearerToken = tokenProvider.getBearerAccessToken(),
            collectionId = collectionId,
            groupId = groupId
        )
    }

    override suspend fun addUpdateItemAmount(collectionId: Int, groupId: Int, itemId: Int, amount: Int): Collection.ItemAmount {
        return serverApi.addUpdateItemAmount(
            bearerToken = tokenProvider.getBearerAccessToken(),
            collectionId = collectionId,
            groupId = groupId,
            itemId = itemId,
            data = AddCollectionItemAmountRequest(amount = amount)
        ).data
    }

    override suspend fun deleteItemAmount(collectionId: Int, groupId: Int, itemId: Int) {
        serverApi.deleteItemAmount(
            bearerToken = tokenProvider.getBearerAccessToken(),
            collectionId = collectionId,
            groupId = groupId,
            itemId = itemId
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
        return serverApi.updateGroupPreferences(
            bearerToken = tokenProvider.getBearerAccessToken(),
            collectionId = collectionId,
            groupId = groupId,
            data = UpdateGroupPreferencesRequest(
                showBaseIngredients = showBaseIngredients,
                collapseIngredients = collapseIngredients,
                costReduction = costReduction,
                itemRecipePreferenceMap = itemRecipePreferenceMap
            )
        ).data
    }
}