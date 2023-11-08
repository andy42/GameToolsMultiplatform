package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.FlowResource
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.local.CollectionLocalSource
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest
import com.jaehl.gameTool.common.data.service.CollectionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


interface CollectionRepo {
    suspend fun getCollectionsFlow(gameId : Int? = null) : FlowResource<List<Collection>>
    suspend fun getCollectionFlow(collectionId : Int) : FlowResource<Collection>
    suspend fun updateCollection(collectionId: Int, body : UpdateCollectionRequest) : Collection

    suspend fun addCollection(data : NewCollectionRequest) : Collection
    suspend fun deleteCollection(collectionId : Int)

    suspend fun updateGroupPreferences(
        collectionId : Int,
        groupId : Int,
        showBaseIngredients : Boolean,
        collapseIngredients : Boolean,
        costReduction : Float,
        itemRecipePreferenceMap : Map<Int, Int?>
    ) : Collection.Group
}

class CollectionRepoImp(
    private val jobDispatcher: JobDispatcher,
    private val collectionService : CollectionService,
    private val collectionLocalSource : CollectionLocalSource
) : CollectionRepo {

    override suspend fun getCollectionsFlow(gameId: Int?): FlowResource<List<Collection>> = flow {
        try {
            emit(Resource.Loading(collectionLocalSource.getCollections(gameId)))
            val collections = collectionService.getCollections(gameId)
            collectionLocalSource.updateCollections(gameId, collections)
            emit(Resource.Success(collections))
        }
        catch (t: Throwable){
            emit(Resource.Error(t))
        }
    }

    override suspend fun getCollectionFlow(collectionId: Int): FlowResource<Collection> = flow {

        try {
            emit(Resource.Loading(collectionLocalSource.getCollection(collectionId)))
            val collection = collectionService.getCollection(collectionId)
            collectionLocalSource.updateCollection(collection)
            emit(Resource.Success(collection))
        }
        catch (t: Throwable){
            emit(Resource.Error(t))
        }
    }

    override suspend fun deleteCollection(collectionId: Int) {
        collectionService.deleteCollection(collectionId)
        collectionLocalSource.deleteCollection(collectionId)
    }

    override suspend fun updateCollection(collectionId: Int, body: UpdateCollectionRequest) : Collection {
        val collection = collectionService.updateCollection(collectionId, body)
        collectionLocalSource.updateCollection(collection)
        return collection
    }

    override suspend fun addCollection(data: NewCollectionRequest): Collection {
        val collection = collectionService.addCollection(data)
        collectionLocalSource.updateCollection(collection)
        return collection
    }

    override suspend fun updateGroupPreferences(
        collectionId: Int,
        groupId: Int,
        showBaseIngredients: Boolean,
        collapseIngredients: Boolean,
        costReduction: Float,
        itemRecipePreferenceMap : Map<Int, Int?>
    ): Collection.Group {
        val group = collectionService.updateGroupPreferences(
            collectionId = collectionId,
            groupId = groupId,
            showBaseIngredients = showBaseIngredients,
            collapseIngredients = collapseIngredients,
            costReduction = costReduction,
            itemRecipePreferenceMap = itemRecipePreferenceMap,
        )
        val collection = collectionLocalSource.getCollection(collectionId)
        val groups = collection.groups.toMutableList()
        val groupIndex = groups.indexOfFirst { it.id ==  groupId}
        groups[groupIndex] = group

        collectionLocalSource.updateCollection(
            collection.copy(
                groups = groups
            )
        )
        return group
    }
}