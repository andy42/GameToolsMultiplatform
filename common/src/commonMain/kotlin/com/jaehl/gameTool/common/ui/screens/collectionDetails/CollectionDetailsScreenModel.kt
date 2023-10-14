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
import com.jaehl.gameTool.common.extensions.toItemAmountViewModel
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.componets.RecipePickerData
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.screens.runWithCatch
import com.jaehl.gameTool.common.ui.util.ItemRecipeInverter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import com.jaehl.gameTool.common.ui.viewModel.RecipeSettings
import kotlinx.coroutines.launch

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

    var dialogState = mutableStateOf<DialogState>(DialogState.Closed)

    private lateinit var config : Config

    fun setup(config : Config) {
        this.config = config

        launchIo(
            jobDispatcher = jobDispatcher,
            onException = {
                System.err.println(it.message)
            }
        ) {
            dataLoad()
        }
    }

    private suspend fun dataLoad(){
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

                val nodes = mergeItemRecipesNodes(items, group.itemRecipePreferenceMap)
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

    private fun onException(t : Throwable) {
        System.err.println(t.message)
    }

    private suspend fun mergeItemRecipesNodes(items : List<ItemAmountViewModel>, itemRecipePreferenceMap : Map<Int, Int?>) : List<ItemRecipeNode>{
        var recipeMap = HashMap<Int, ItemAmount>()
        items.forEach { item ->
            val recipe = recipeRepo.getRecipe(getRecipeIdForItem(item.itemModel.id, itemRecipePreferenceMap) ?: return@forEach)

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
            itemRecipeNodeUtil.buildTree(it, null, itemRecipePreferenceMap = itemRecipePreferenceMap)
        }
    }

    fun onRecipeSettingDialogStateClick(groupId : Int) = runWithCatch(::onException ) {
        dialogState.value = DialogState.RecipeSettingsDialog(
            groupId = groupId,
            settings = groupsMap[groupId]?.recipeSettings ?: throw Exception("groupId not found groupId")
        )
    }

    fun onCloseDialog(){
        dialogState.value = DialogState.Closed
    }

    fun onRecipeSettingsChange(groupId : Int, recipeSettings : RecipeSettings) = launchIo(jobDispatcher, ::onException) {

        var group = groupsMap[groupId]?.copy(
            recipeSettings = recipeSettings
        ) ?: throw Exception("groupId not found groupId")

        collectionRepo.updateGroupPreferences(
            collectionId =  config.collectionId,
            groupId = groupId,
            showBaseIngredients = recipeSettings.showBaseIngredients,
            collapseIngredients = recipeSettings.collapseIngredients,
            costReduction = 1f,
            itemRecipePreferenceMap = group.itemRecipePreferenceMap
        )
        groupsMap[groupId] = group

        dialogState.value = DialogState.RecipeSettingsDialog(
            groupId = groupId,
            settings = group.recipeSettings
        )

        groups.postSwap(
            groupsMap.values.toList()
        )
    }

    private suspend fun getRecipeIdForItem(itemId : Int, itemRecipePreferenceMap: Map<Int, Int?>) : Int? {
        if(itemRecipePreferenceMap.containsKey(itemId)) {
            return itemRecipePreferenceMap[itemId]
        } else {
            return recipeRepo.getRecipesForOutput(itemId).firstOrNull()?.id
        }
    }

    fun onRecipeChangeClick(itemId : Int, groupId : Int) = launchIo(jobDispatcher, ::onException){

        val recipePickerData = RecipePickerData(
            selectedRecipeId = getRecipeIdForItem(itemId, groupsMap[groupId]?.itemRecipePreferenceMap ?: hashMapOf()),
            recipes = recipeRepo.getRecipesForOutput(itemId)
                .map { recipe ->
                    RecipePickerData.RecipeViewModel(
                        id = recipe.id,
                        input = recipe.input.map {
                            it.toItemAmountViewModel(itemRepo, appConfig, tokenProvider)
                        },
                        output = recipe.output.map {
                            it.toItemAmountViewModel(itemRepo, appConfig, tokenProvider)
                        }
                    )
                }
        )
        dialogState.value = DialogState.RecipePickerDialog(
            itemId = itemId,
            groupId = groupId,
            recipePickerData = recipePickerData
        )
    }

    fun onRecipePickerSelectedClick(dialogState : DialogState.RecipePickerDialog, recipeId : Int?) {
        this.dialogState.value = dialogState.copy(
            recipePickerData = dialogState.recipePickerData.copy(
                selectedRecipeId = recipeId
            )
        )
    }

    fun onGroupItemRecipeChanged(itemId : Int, groupId : Int, recipeId : Int?) = launchIo(jobDispatcher, ::onException){
        val groupsViewModel = groupsMap[groupId] ?: throw Exception("group not found")
        val itemRecipePreferenceMap = groupsViewModel.itemRecipePreferenceMap.toMutableMap()
        itemRecipePreferenceMap[itemId] = recipeId
        groupsMap[groupId] = groupsViewModel.copy(
            itemRecipePreferenceMap = itemRecipePreferenceMap
        )
        collectionRepo.updateGroupPreferences(
            collectionId =  config.collectionId,
            groupId = groupId,
            showBaseIngredients = groupsViewModel.recipeSettings.showBaseIngredients,
            collapseIngredients = groupsViewModel.recipeSettings.collapseIngredients,
            costReduction = 1f,
            itemRecipePreferenceMap = itemRecipePreferenceMap
        )
        dataLoad()
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
        val baseNodes : List<ItemRecipeNode>,
        val itemRecipePreferenceMap: Map<Int, Int?>
    )

    sealed class DialogState{
        data object Closed : DialogState()
        data class RecipeSettingsDialog(
            val groupId : Int,
            val settings : RecipeSettings
        ) : DialogState()

        data class RecipePickerDialog(
            val itemId : Int,
            val groupId : Int,
            val recipePickerData : RecipePickerData
        ) : DialogState()
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
            showBaseIngredients = this.showBaseIngredients,
            collapseIngredients = this.collapseIngredients
        ),
        itemList = this.itemAmounts.map {
            ItemAmountViewModel(
                itemModel = itemRepo.getItem(it.itemId)?.toItemModel(appConfig, tokenProvider) ?: throw Exception("Item Not Found : ${it.itemId}"),
                amount = it.amount
            )
        },
        nodes = nodes,
        baseNodes = baseNodes,
        itemRecipePreferenceMap = this.itemRecipePreferenceMap
    )
}