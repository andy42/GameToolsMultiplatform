package com.jaehl.gameTool.localSourceSqlDelight.data.local

import com.jaehl.gameTool.common.data.local.CollectionLocalSource
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.localSourceSqlDelight.Database

class CollectionLocalSourceSqlDelight(
    private val database: Database
) : CollectionLocalSource {

    private fun createCollectionItemAmount(groupId : Int) : List<Collection.ItemAmount> {
        return database.collectionQueries.getCollectionItemAmounts(groupId).executeAsList().map {
            Collection.ItemAmount(
                itemId = it.item_id,
                amount = it.amount
            )
        }
    }

    private fun createItemRecipePreferenceMap(groupId : Int) : Map<Int, Int?> {
        val preferenceMap = HashMap<Int, Int?>()
        database.collectionQueries.getItemRecipePreferences(groupId).executeAsList().forEach {
            preferenceMap[it.item_id] = it.recipe_id_preference
        }
        return preferenceMap
    }

    private fun createCollectionGroup(collectionId : Int) : List<Collection.Group> {
        return database.collectionQueries.getCollectionGroups(collectionId).executeAsList().map { collectionGroupEntity ->
            Collection.Group(
                id = collectionGroupEntity.id,
                collectionId = collectionGroupEntity.collection_id,
                name = collectionGroupEntity.name,
                itemAmounts = createCollectionItemAmount(collectionGroupEntity.id),
                showBaseIngredients = collectionGroupEntity.show_base_ingredients,
                collapseIngredients = collectionGroupEntity.collapse_ingredients,
                costReduction = collectionGroupEntity.cost_Reduction,
                itemRecipePreferenceMap = createItemRecipePreferenceMap(collectionGroupEntity.id)
            )
        }
    }

    private fun toCollection(collectionEntity : CollectionEntity) : Collection {
        return Collection(
            id = collectionEntity.id,
            name = collectionEntity.name,
            userId = collectionEntity.user_id,
            gameId = collectionEntity.game_id,
            groups = createCollectionGroup(collectionEntity.id)
        )
    }

    override suspend fun getCollections(gameId: Int?): List<Collection> {
        val collection = if(gameId == null) database.collectionQueries.getAllCollections().executeAsList()
            else database.collectionQueries.getAllCollectionsForGame(gameId).executeAsList()

        return collection.map {
            toCollection(it)
        }
    }

    override suspend fun getCollection(collectionId: Int): Collection {
        return toCollection(
            database.collectionQueries.getCollection(collectionId).executeAsOne()
        )
    }

    override suspend fun deleteCollection(collectionId: Int) {
        database.collectionQueries.deleteCollection(collectionId)
    }

    private fun updateCollectionRequest(collection: Collection)  {
        database.collectionQueries.updateCollection( CollectionEntity(
                id = collection.id,
                name = collection.name,
                user_id = collection.userId,
                game_id = collection.gameId
            )
        )
        database.collectionQueries.deleteCollectionGroup(collection.id)
        collection.groups.forEach { group ->
            database.collectionQueries.deleteCollectionItemAmounts(group.id)
            database.collectionQueries.deleteItemRecipePreferences(group.id)

            database.collectionQueries.updateCollectionGroup(
                CollectionGroupEntity(
                    id = group.id,
                    collection_id = group.collectionId,
                    name = group.name,
                    show_base_ingredients = group.showBaseIngredients,
                    collapse_ingredients = group.collapseIngredients,
                    cost_Reduction = group.costReduction
                )
            )
            group.itemAmounts.forEach { itemAmount ->
                database.collectionQueries.updateCollectionItemAmount(
                    CollectionItemAmountEntity(
                        group_id = group.id,
                        item_id = itemAmount.itemId,
                        amount = itemAmount.amount
                    )
                )
            }
            group.itemRecipePreferenceMap.entries.forEach { (itemId, recipeId) ->
                database.collectionQueries.updateItemRecipePreference(
                    ItemRecipePreferenceEntity(
                        group_id = group.id,
                        item_id = itemId,
                        recipe_id_preference = recipeId
                    )
                )
            }
        }
    }

    override suspend fun updateCollection(collection: Collection) {
        database.transaction {
            updateCollectionRequest(collection)
        }
    }

    override suspend fun updateCollections(gameId: Int?, collections: List<Collection>) {
        database.transaction {
            collections.forEach { collection ->
                updateCollectionRequest(collection)
            }
        }
    }
}

