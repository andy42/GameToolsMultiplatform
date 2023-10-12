package com.jaehl.gameTool.common.ui.screens.collectionDetails

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import com.jaehl.gameTool.common.data.repo.CollectionRepo
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.RecipeRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.screens.runWithCatch
import com.jaehl.gameTool.common.ui.util.ItemRecipeInverter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import com.jaehl.gameTool.common.ui.viewModel.RecipeSettings

class CollectionDetailsScreenModel (
    val jobDispatcher : JobDispatcher,
    val collectionRepo : CollectionRepo,
    val itemRepo : ItemRepo,
    val recipeRepo: RecipeRepo,
    val appConfig : AppConfig,
    val tokenProvider: TokenProvider,
    val itemRecipeNodeUtil : ItemRecipeNodeUtil,
    var itemRecipeInverter : ItemRecipeInverter
) : ScreenModel {

    var title = mutableStateOf("")
    private val groupsMap = hashMapOf<Int, GroupsViewModel>()
    var groups = mutableStateListOf<GroupsViewModel>()

    var recipeSettingDialogState = mutableStateOf<RecipeSettingDialogState>(RecipeSettingDialogState.Closed)

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

            groupsMap.clear()
            collectionRepo.getCollectionFlow(config.collectionId).collect{ collection ->
                title.value = collection.name

                collection.groups.map { group ->
                    val items = group.itemAmounts.mapNotNull { itemIngredient ->
                        val item = itemRepo.getItem(itemIngredient.itemId) ?: return@mapNotNull null
                        ItemAmountViewModel(
                            item.toItemModel(appConfig, tokenProvider),
                            itemIngredient.amount
                        )
                    }

                    val nodes = mergeItemRecipesNodes(items)
                    val baseNodes = itemRecipeInverter.invertItemRecipes(nodes)

                    group.toGroupsViewModel(
                        itemRepo,
                        appConfig,
                        tokenProvider,
                        nodes,
                        baseNodes
                    )
                }.forEach {
                    groupsMap[it.id] = it
                }

                groups.postSwap(
                    groupsMap.values.toList()
                )
            }
        }
    }

    private fun onException(t : Throwable) {
        System.err.println(t.message)
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

    fun onRecipeSettingDialogStateClick(groupId : Int) = runWithCatch(::onException ) {
        recipeSettingDialogState.value = RecipeSettingDialogState.Open(
            groupId = groupId,
            settings = groupsMap[groupId]?.recipeSettings ?: throw Exception("groupId not found groupId")
        )
    }

    fun onRecipeSettingDialogStateClose(){
        recipeSettingDialogState.value = RecipeSettingDialogState.Closed
    }

    fun onRecipeSettingsChange(groupId : Int, recipeSettings : RecipeSettings) = launchIo(jobDispatcher, ::onException) {

        var group = groupsMap[groupId]?.copy(
            recipeSettings = recipeSettings
        ) ?: throw Exception("groupId not found groupId")

        groupsMap[groupId] = group

        recipeSettingDialogState.value = RecipeSettingDialogState.Open(
            groupId = groupId,
            settings = group.recipeSettings
        )

        groups.postSwap(
            groupsMap.values.toList()
        )
    }

    data class Config(
        val gameId : Int,
        val collectionId : Int
    )

    data class GroupsViewModel(
        val id : Int,
        val name : String,
        val recipeSettings : RecipeSettings,
        val itemList : List<ItemAmountViewModel>,
        val nodes : List<ItemRecipeNode>,
        val baseNodes : List<ItemRecipeNode>
    )

    sealed class RecipeSettingDialogState{
        data object Closed : RecipeSettingDialogState()
        data class Open(
            val groupId : Int,
            val settings : RecipeSettings
        ) : RecipeSettingDialogState()
    }
}

suspend fun Collection.Group.toGroupsViewModel(
    itemRepo : ItemRepo,
    appConfig : AppConfig,
    tokenProvider: TokenProvider,
    nodes : List<ItemRecipeNode>,
    baseNodes : List<ItemRecipeNode>,
) : CollectionDetailsScreenModel.GroupsViewModel {
    return CollectionDetailsScreenModel.GroupsViewModel(
        id = this.id,
        name = this.name,
        recipeSettings = RecipeSettings(
            showBaseIngredients = false,
            collapseIngredients = true
        ),
        itemList = this.itemAmounts.map {
            ItemAmountViewModel(
                itemModel = itemRepo.getItem(it.itemId)?.toItemModel(appConfig, tokenProvider) ?: throw Exception("Item Not Found : ${it.itemId}"),
                amount = it.amount
            )
        },
        nodes = nodes,
        baseNodes = baseNodes
    )
}