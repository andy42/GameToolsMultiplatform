package com.jaehl.gameTool.common.ui.screens.collectionDetails

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import com.jaehl.gameTool.common.data.repo.CollectionRepo
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.RecipeRepo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.util.ItemRecipeInverter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel

class CollectionDetailsScreenModel (
    val jobDispatcher : JobDispatcher,
    val collectionRepo : CollectionRepo,
    val itemRepo : ItemRepo,
    val recipeRepo: RecipeRepo,
    val appConfig : AppConfig,
    val authProvider: AuthProvider,
    val itemRecipeNodeUtil : ItemRecipeNodeUtil,
    var itemRecipeInverter : ItemRecipeInverter
) : ScreenModel {

    var title = mutableStateOf("")
    var groups = mutableStateListOf<GroupsViewModel>()

    private lateinit var config : Config

    fun setup(config : Config) {
        this.config = config

        launchIo(
            jobDispatcher = jobDispatcher,
            onException = {
                System.err.println(it.message)
            }
        ) {
            recipeRepo.preloadRecipes(config.gameId)
            itemRepo.preloadItems(config.gameId)

            collectionRepo.getCollectionFlow(config.collectionId).collect{ collection ->
                title.value = collection.name
                groups.postSwap(
                    collection.groups.map { group ->
                        val items = group.itemAmounts.mapNotNull { itemIngredient ->
                            val item = itemRepo.getItem(itemIngredient.itemId) ?: return@mapNotNull null
                            ItemAmountViewModel(
                                item.toItemModel(appConfig, authProvider),
                                itemIngredient.amount
                            )
                        }

                        val nodes = mergeItemRecipesNodes(items)
                        val baseNodes = itemRecipeInverter.invertItemRecipes(nodes)

                        group.toGroupsViewModel(
                            itemRepo,
                            appConfig,
                            authProvider,
                            nodes,
                            baseNodes
                        )
                    }
                )
            }
        }
    }

    private suspend fun mergeItemRecipesNodes(items : List<ItemAmountViewModel>) : List<ItemRecipeNode>{
        var recipeMap = HashMap<Int, ItemAmount>()
        items.forEach { item ->
            val recipe = recipeRepo.getRecipesForOutput(inputItemId = item.itemModel.id).firstOrNull() ?: return@forEach
            recipe.input.forEach {
                if(recipeMap.containsKey(it.itemId)){
                    recipeMap[it.itemId]?.amount = recipeMap[it.itemId]!!.amount + it.amount*item.amount
                } else {
                    recipeMap[it.itemId] = it.copy()
                    recipeMap[it.itemId]?.amount = it.amount*item.amount
                }
            }
        }
        return recipeMap.values.toList().mapNotNull {
            itemRecipeNodeUtil.buildTree(it, null)
        }
    }

    data class Config(
        val gameId : Int,
        val collectionId : Int
    )

    data class GroupsViewModel(
        val id : Int,
        val name : String,
        var collapseIngredientList : Boolean,
        var showBaseCrafting : Boolean,
        val itemList : List<ItemAmountViewModel>,
        val nodes : List<ItemRecipeNode>,
        val baseNodes : List<ItemRecipeNode>
    )
}

fun Collection.Group.toGroupsViewModel(
    itemRepo : ItemRepo,
    appConfig : AppConfig,
    authProvider: AuthProvider,
    nodes : List<ItemRecipeNode>,
    baseNodes : List<ItemRecipeNode>,
) : CollectionDetailsScreenModel.GroupsViewModel {
    return CollectionDetailsScreenModel.GroupsViewModel(
        id = this.id,
        name = this.name,
        collapseIngredientList =  true,
        showBaseCrafting = true,
        itemList = this.itemAmounts.map {
            ItemAmountViewModel(
                itemModel = itemRepo.getItem(it.itemId)?.toItemModel(appConfig, authProvider) ?: throw Exception("Item Not Found : ${it.itemId}"),
                amount = it.amount
            )
        },
        nodes = nodes,
        baseNodes = baseNodes
    )
}