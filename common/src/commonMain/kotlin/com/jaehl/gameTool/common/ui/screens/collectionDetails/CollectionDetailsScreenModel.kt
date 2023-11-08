package com.jaehl.gameTool.common.ui.screens.collectionDetails

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.*
import com.jaehl.gameTool.common.data.model.Collection
import com.jaehl.gameTool.common.data.repo.CollectionRepo
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.RecipeRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.extensions.toItemAmountViewModel
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.componets.RecipePickerData
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.screens.runWithCatch
import com.jaehl.gameTool.common.ui.util.ItemRecipeInverter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

class CollectionDetailsScreenModel (
    val jobDispatcher : JobDispatcher,
    val collectionRepo : CollectionRepo,
    val itemRepo : ItemRepo,
    val recipeRepo: RecipeRepo,
    val appConfig : AppConfig,
    val tokenProvider: TokenProvider,
    val itemRecipeNodeUtil : ItemRecipeNodeUtil,
    var itemRecipeInverter : ItemRecipeInverter,
    val uiExceptionHandler: UiExceptionHandler
) : ScreenModel {

    var title = mutableStateOf("")
    private val groupsMap = hashMapOf<Int, GroupsViewModel>()
    var groups = mutableStateListOf<GroupsViewModel>()

    val dialogViewModel = mutableStateOf<DialogViewModel>(ClosedDialogViewModel)

    var pageLoading = mutableStateOf(false)

    private lateinit var config : Config

    fun setup(config : Config) {
        this.config = config
        dataRefresh()
    }

    fun dataRefresh() {

        launchIo(jobDispatcher, ::onException) {
            combine(
                collectionRepo.getCollectionFlow(config.collectionId),
                itemRepo.getItems(config.gameId),
                recipeRepo.getRecipesFlow(config.gameId)
            ) { collectionResource : Resource<Collection>, itemResource: Resource<List<Item>>, recipesResource: Resource<List<Recipe>> ->
                updateUi(collectionResource, itemResource, recipesResource)
            }.collect()
        }
    }

    private fun updateRecipeOutputArray(array : ArrayList<Recipe>?, recipe : Recipe) : ArrayList<Recipe>{
        val array = array ?: arrayListOf()
        array.add(recipe)
        return array
    }

    private suspend fun updateUi(
        collectionResource : Resource<Collection>,
        itemResource : Resource<List<Item>>,
        recipesResource : Resource<List<Recipe>>
    ){
        pageLoading.value = (collectionResource is Resource.Loading
                || itemResource is Resource.Loading
                || recipesResource is Resource.Loading)

        //if the Resource are empty might not have  loaded wait for server response
        if(itemResource is Resource.Loading && itemResource.data.isEmpty()) return
        if(recipesResource is Resource.Loading && recipesResource.data.isEmpty()) return

        listOf<Resource<*>>(collectionResource, itemResource, recipesResource).forEach {
            if(it is Resource.Error){
                onException(it.exception)
                return
            }
        }

        val collection = collectionResource.getDataOrThrow()
        val recipeMap = HashMap<Int, Recipe>()

        val recipeOutputMap = LinkedHashMap<Int, ArrayList<Recipe>>()
        recipesResource.getDataOrThrow().forEach { recipe ->
            recipe.output.forEach {itemAmount ->
                recipeOutputMap[itemAmount.itemId] = updateRecipeOutputArray(recipeOutputMap[itemAmount.itemId], recipe)
            }
            recipeMap[recipe.id] = recipe
        }

        val itemMap = HashMap<Int, Item>()
        itemResource.getDataOrThrow().forEach { item ->
            itemMap[item.id] = item
        }

        title.value = collection.name

        collection.groups.map { group ->
            val items = group.itemAmounts.mapNotNull { itemIngredient ->
                val item = itemMap[itemIngredient.itemId] ?: return@mapNotNull null
                ItemAmountViewModel(
                    item.toItemModel(appConfig, tokenProvider),
                    itemIngredient.amount
                )
            }

            val nodes = mergeItemRecipesNodes(
                items,
                group.itemRecipePreferenceMap,
                getRecipesForOutput = {itemId ->
                    recipeOutputMap[itemId] ?: listOf()
                },
                getRecipe = { recipeId ->
                    recipeMap[recipeId]
                }
            )
            val baseNodes = itemRecipeInverter.invertItemRecipes(nodes)

            group.toGroupsViewModel(
                getItem = { itemId ->
                    itemMap[itemId] ?: throw UiException.NotFound("item 2 not found : $itemId")
                },
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

    private fun onException(t : Throwable) {
        System.err.println(t.message)
        dialogViewModel.value = uiExceptionHandler.handelException(t)
        pageLoading.value = false
    }

    private suspend fun mergeItemRecipesNodes(
        items : List<ItemAmountViewModel>,
        itemRecipePreferenceMap : Map<Int, Int?>,
        getRecipesForOutput : (itemId : Int) -> List<Recipe>,
        getRecipe : (recipeId : Int) -> Recipe?
    ) : List<ItemRecipeNode, >{

        var recipeMap = HashMap<Int, ItemAmount>()
        items.forEach { item ->
            val recipe = getRecipe(getRecipeIdForItem(item.itemModel.id, itemRecipePreferenceMap) ?: return@forEach) ?: return@forEach

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
            itemRecipeNodeUtil.buildTree(it, null, itemRecipePreferenceMap = itemRecipePreferenceMap, getRecipesForOutput= getRecipesForOutput)
        }
    }

    fun onRecipeSettingDialogStateClick(groupId : Int) = runWithCatch(::onException ) {
        dialogViewModel.value = RecipeSettingsDialog(
            groupId = groupId,
            settings = groupsMap[groupId]?.recipeSettings ?: throw Exception("groupId not found groupId")
        )
    }

    fun closeDialog(){
        dialogViewModel.value = ClosedDialogViewModel
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

        dialogViewModel.value = RecipeSettingsDialog(
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
            return recipeRepo.getRecipesForOutputCached(config.gameId, itemId).firstOrNull()?.id
        }
    }

    fun onRecipeChangeClick(itemId : Int, groupId : Int) = launchIo(jobDispatcher, ::onException){

        val recipePickerData = RecipePickerData(
            selectedRecipeId = getRecipeIdForItem(itemId, groupsMap[groupId]?.itemRecipePreferenceMap ?: hashMapOf()),
            recipes = recipeRepo.getRecipesForOutputCached(config.gameId, itemId)
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
        dialogViewModel.value = RecipePickerDialog(
            itemId = itemId,
            groupId = groupId,
            recipePickerData = recipePickerData
        )
    }

    fun onRecipePickerSelectedClick(dialogState : RecipePickerDialog, recipeId : Int?) {
        this.dialogViewModel.value = dialogState.copy(
            recipePickerData = dialogState.recipePickerData.copy(
                selectedRecipeId = recipeId
            )
        )
    }

    fun onGroupItemRecipeChanged(itemId : Int, groupId : Int, recipeId : Int?) = launchIo(jobDispatcher, ::onException){
        pageLoading.value = true
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
        dataRefresh()
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

    data class RecipeSettingsDialog(
        val groupId : Int,
        val settings : RecipeSettings
    ) : DialogViewModel

    data class RecipePickerDialog(
        val itemId : Int,
        val groupId : Int,
        val recipePickerData : RecipePickerData
    ) : DialogViewModel
}

suspend fun Collection.Group.toGroupsViewModel(
    getItem : (itemId : Int) -> Item,
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
                itemModel = getItem(it.itemId).toItemModel(appConfig, tokenProvider),
                amount = it.amount
            )
        },
        nodes = nodes,
        baseNodes = baseNodes,
        itemRecipePreferenceMap = this.itemRecipePreferenceMap
    )
}