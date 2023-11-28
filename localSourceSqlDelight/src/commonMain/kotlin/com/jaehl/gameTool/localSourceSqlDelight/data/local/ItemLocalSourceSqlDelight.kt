package com.jaehl.gameTool.localSourceSqlDelight.data.local

import com.jaehl.gameTool.common.data.local.ItemLocalSource
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.localSourceSqlDelight.Database

class ItemLocalSourceSqlDelight (
    private val database: Database
) : ItemLocalSource {

    private fun getItemCategories(itemId : Int) : List<ItemCategory> {
        return database.itemsQueries.getItemCategories(itemId).executeAsList().mapNotNull { itemCategory ->
            ItemCategory(
                id = itemCategory.id,
                name = itemCategory.name
            )
        }
    }

    override suspend fun getItems(gameId: Int?): List<Item> {
        return if(gameId == null){
            database.itemsQueries.getAllItems().executeAsList().map {  itemEntity ->
                itemEntity.toItem( getItemCategories(itemEntity.id))
            }
        } else {
            database.itemsQueries.getAllItemsForGame(gameId).executeAsList().map {  itemEntity ->
                itemEntity.toItem( getItemCategories(itemEntity.id))
            }
        }
    }

    override suspend fun getItem(itemId: Int): Item {
        val itemEntity = database.itemsQueries.getItem(itemId).executeAsOne()
        return itemEntity.toItem(getItemCategories(itemEntity.id))
    }

    override suspend fun updateItem(item: Item) {
        database.transaction {
            database.itemsQueries.updateItem(item.toItemEntity())
            database.itemsQueries.deleteItemCategories(itemId = item.id)
            item.categories.forEach { itemCategory ->
                database.itemsQueries.updateItemCategory(
                    ItemCategoryEntity(
                        item_id = item.id,
                        category_id = itemCategory.id
                    )
                )
            }
        }
    }

    override suspend fun updateItems(gameId: Int?, items: List<Item>) {
        database.transaction {
            if (gameId == null) {
                database.itemsQueries.deleteAllItems()
            } else {
                database.itemsQueries.deleteItemsForGame(gameId)
            }

            items.forEach { item ->
                database.itemsQueries.updateItem(item.toItemEntity())
                database.itemsQueries.deleteItemCategories(itemId = item.id)
                item.categories.forEach { itemCategory ->
                    database.itemsQueries.updateItemCategory(
                        ItemCategoryEntity(
                            item_id = item.id,
                            category_id = itemCategory.id
                        )
                    )
                }
            }
        }
    }

    override suspend fun getItemCategories(): List<ItemCategory> {
        return database.itemsQueries.selectAllCategories().executeAsList().map {
            ItemCategory(
                id = it.id,
                name = it.name
            )
        }
    }

    override suspend fun addItemCategory(itemCategory: ItemCategory) {
        database.itemsQueries.updateCategories(
            CategoryEntity(
                id = itemCategory.id,
                name = itemCategory.name
            )
        )
    }

    override suspend fun addItemCategories(itemCategories: List<ItemCategory>) {
        database.transaction {
            itemCategories.forEach { itemCategory ->
                database.itemsQueries.updateCategories(
                    CategoryEntity(
                        id = itemCategory.id,
                        name = itemCategory.name
                    )
                )
            }
        }
    }
}

fun ItemEntity.toItem(categories : List<ItemCategory>) : Item {
    return Item(
        id = this.id,
        game = this.game_id,
        name = this.name,
        categories = categories,
        image = this.image
    )
}

fun Item.toItemEntity() : ItemEntity {
    return ItemEntity(
        id = this.id,
        game_id = this.game,
        name = this.name,
        image = this.image
    )
}