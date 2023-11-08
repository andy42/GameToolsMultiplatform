package com.jaehl.gameTool.common.data.repo

import com.jaehl.gameTool.common.data.FlowResource
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.request.NewCollectionRequest
import com.jaehl.gameTool.common.data.model.request.UpdateCollectionRequest
import kotlinx.coroutines.flow.flow

class CollectionRepoMock : CollectionRepo {

    var collectionList = arrayListOf<Collection>()

    var getCollectionsError : Resource.Error? = null
    var getCollectionError : Resource.Error? = null


    override suspend fun getCollectionsFlow(gameId: Int?): FlowResource<List<Collection>>  = flow {
        getCollectionsError?.let {
            emit(it)
            return@flow
        }
        emit(Resource.Success(collectionList))
    }

    override suspend fun getCollectionFlow(collectionId: Int): FlowResource<Collection> = flow {
        getCollectionError?.let {
            emit(it)
            return@flow
        }
        emit(Resource.Success(
            collectionList.first {it.id == collectionId}
        ))
    }

    var userId = 0
    var lastGroupId = 0

    private fun getUniqueGroupId() : Int = lastGroupId++

    override suspend fun updateCollection(collectionId: Int, body: UpdateCollectionRequest): Collection {

        val collectionIndex= collectionList.indexOfFirst { it.id == collectionId }

        val groups = collectionList[collectionIndex].groups.toMutableList()
        val iterator = groups.iterator()

       body.groups?.let { updateGroups ->
           while (iterator.hasNext()){
               val group = iterator.next()
               if (updateGroups.firstOrNull { it.id == group.id} == null ){
                   groups.removeIf { it.id == group.id }
               }
           }
           updateGroups.forEach { updateGroup ->
               if(updateGroup.id == null){
                   groups.add(
                       Collection.Group(
                           id = getUniqueGroupId(),
                           collectionId = collectionId,
                           name = updateGroup.name,
                           itemAmounts = updateGroup.itemAmounts,
                           showBaseIngredients = false,
                           collapseIngredients = true,
                           costReduction = 1f,
                           itemRecipePreferenceMap = mapOf()
                       )
                   )
               }
               val groupIndex = groups.indexOfFirst { it.id ==  updateGroup.id}
               val oldGroup = groups[groupIndex]
               groups[groupIndex] = oldGroup.copy(
                   collectionId = collectionId,
                   name = updateGroup.name,
                   itemAmounts = updateGroup.itemAmounts
               )
           }
       }

        val collection = collectionList[collectionIndex].copy(
            name = body.name,
            groups = groups

        )
        collectionList[collectionIndex] = collection
        return collection
    }

    override suspend fun addCollection(data: NewCollectionRequest): Collection {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCollection(collectionId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateGroupPreferences(
        collectionId: Int,
        groupId: Int,
        showBaseIngredients: Boolean,
        collapseIngredients: Boolean,
        costReduction: Float,
        itemRecipePreferenceMap: Map<Int, Int?>
    ): Collection.Group {
        TODO("Not yet implemented")
    }
}