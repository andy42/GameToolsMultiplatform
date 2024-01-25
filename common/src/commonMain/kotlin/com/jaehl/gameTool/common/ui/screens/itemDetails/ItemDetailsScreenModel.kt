package com.jaehl.gameTool.common.ui.screens.itemDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import com.jaehl.gameTool.common.data.model.Recipe
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.RecipeRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.extensions.toItemAmountViewModel
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.componets.RecipePickerData
import com.jaehl.gameTool.common.ui.screens.runWithCatch
import com.jaehl.gameTool.common.ui.util.ItemRecipeFlattener
import com.jaehl.gameTool.common.ui.util.ItemRecipeInverter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

class ItemDetailsScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val tokenProvider: TokenProvider,
    private val itemRepo: ItemRepo,
    private val userRepo: UserRepo,
    private val recipeRepo: RecipeRepo,
    private val appConfig: AppConfig,
    private val itemRecipeNodeUtil : ItemRecipeNodeUtil,
    private val itemRecipeInverter: ItemRecipeInverter,
    private val itemRecipeFlattener: ItemRecipeFlattener,
    private val uiExceptionHandler : UiExceptionHandler
) : ScreenModel {

    private lateinit var config : Config

    var showEditItems by mutableStateOf(false)

    var dialogViewModel by mutableStateOf<DialogViewModel>(ClosedDialogViewModel)

    var pageLoading by mutableStateOf(false)
    var itemInfo by mutableStateOf(ItemInfoModel())

    private val itemRecipePreferenceMap =  hashMapOf<Int, Int?>()

    private val recipeMap = hashMapOf<Int, RecipeViewModel>()
    var recipeModels = mutableStateListOf<RecipeViewModel>()

    private val recipePreferencesMap = hashMapOf<Int, RecipeSettings>()

    var usedAsInputRecipes = mutableStateListOf<RecipeSimpleViewModel>()

    fun update(config : Config, ifItemChanged : Boolean = false) {
        if(ifItemChanged && this.config.itemId == config.itemId){
            return
        }

        this.config = config
        itemRecipePreferenceMap.clear()
        recipePreferencesMap.clear()
        dataRefresh()
    }

    private fun updateRecipeOutputArray(array : ArrayList<Recipe>?, recipe : Recipe) : ArrayList<Recipe>{
        val array = array ?: arrayListOf()
        array.add(recipe)
        return array
    }

    private suspend fun updateUi(
        userResource : Resource<User>,
        itemResource : Resource<List<Item>>,
        recipesResource : Resource<List<Recipe>>
    ){
        pageLoading = (userResource is Resource.Loading || itemResource is Resource.Loading || recipesResource is Resource.Loading)

        listOf<Resource<*>>(userResource, itemResource, recipesResource).forEach {
            if(it is Resource.Error){
                onException(it.exception)
                return
            }
        }

        val itemMap = itemResource.getDataOrThrow().associateBy { it.id }

        val item = itemMap[config.itemId] ?: throw UiException.NotFound("item not found : ${config.itemId}")

        val recipeOutputMap = LinkedHashMap<Int, ArrayList<Recipe>>()
        recipesResource.getDataOrThrow().forEach { recipe ->
            recipe.output.forEach {itemAmount ->
                recipeOutputMap[itemAmount.itemId] = updateRecipeOutputArray(recipeOutputMap[itemAmount.itemId], recipe)
            }
        }

        val recipeInputList = HashSet<Recipe>()
        recipesResource.getDataOrThrow().forEach { recipe ->
            recipe.input.forEach {itemAmount ->
                if(itemAmount.itemId == config.itemId){
                    recipeInputList.add(recipe)
                    return@forEach
                }
            }
        }

        usedAsInputRecipes.postSwap(
            recipeInputList.map { recipe ->
                RecipeSimpleViewModel(
                    id = recipe.id,
                    inputs = recipe.input.map { itemAmount ->
                        ItemAmountViewModel(
                            itemModel = itemMap[itemAmount.itemId]?.toItemModel(appConfig, tokenProvider) ?: throw Exception("itemId not found : ${itemAmount.itemId}"),
                            amount = itemAmount.amount
                        )
                    },
                    outputs = recipe.output.map { itemAmount ->
                        ItemAmountViewModel(
                            itemModel = itemMap[itemAmount.itemId]?.toItemModel(appConfig, tokenProvider) ?: throw Exception("itemId not found : ${itemAmount.itemId}"),
                            amount = itemAmount.amount
                        )
                    },
                    craftedAt = recipe.craftedAt.map { itemId ->
                        itemMap[itemId]?.toItemModel(appConfig, tokenProvider) ?: throw Exception("itemId not found : $itemId")
                    }
                )
            }
        )

        showEditItems = listOf(
            User.Role.Admin,
            User.Role.Contributor
        ).contains(userResource.getDataOrThrow().role)



        itemInfo = item.toItemInfoModel(appConfig, tokenProvider)

        val recipes = recipeOutputMap[item.id] ?: listOf()
        recipeMap.clear()
        recipes.mapNotNull { recipe ->
            val node = itemRecipeNodeUtil.buildTree(
                itemAmount= ItemAmountViewModel(
                    itemModel = item.toItemModel(appConfig, tokenProvider),
                    amount = recipe.output.first { it.itemId == config.itemId}.amount
                ),
                parentNode = null,
                recipeId = recipe.id,
                itemRecipePreferenceMap = itemRecipePreferenceMap,
                getRecipesForOutput = { itemId : Int ->
                    recipeOutputMap[itemId]?.toList() ?: listOf<Recipe>()
                }
            ) ?: return@mapNotNull null

            val baseIngredients = itemRecipeInverter.invertItemRecipes(listOf(node))
            RecipeViewModel(
                id = recipe.id,
                node = node,
                recipeSettings = recipePreferencesMap[recipe.id] ?: RecipeSettings(
                    displayType = RecipeDisplayType.Normal,
                    collapseIngredients = true
                ),
                baseIngredients = baseIngredients,
                flatRecipeItems = itemRecipeFlattener.flattenItemRecipes(listOf(node)),
                craftedAt = node.recipe?.craftedAt?.mapNotNull {
                    itemMap[it]?.toItemModel(appConfig, tokenProvider)
                } ?: listOf()
            )
        }.forEach {
            recipeMap[it.id] = it
        }

        recipeModels.postSwap(recipeMap.values.toList())
    }

    fun dataRefresh() {

        launchIo(jobDispatcher, ::onException){

            combine(
                userRepo.getUserSelFlow(),
                itemRepo.getItems(config.gameId),
                recipeRepo.getRecipesFlow(config.gameId)
            ) { userResource : Resource<User>, itemResource : Resource<List<Item>>, recipesResource : Resource<List<Recipe>> ->

                updateUi(userResource, itemResource, recipesResource)
            }.collect()
        }
    }

    private fun onException(t: Throwable){
        if (t is UiException){
            dialogViewModel = uiExceptionHandler.handelException(t)
        }
        else {
            dialogViewModel = ErrorDialogViewModel(
                title = "Error",
                message = "Oops something went wrong"
            )
        }
        System.err.println(t)
        pageLoading = false
    }

    data class Config(
        val gameId : Int,
        val itemId : Int
    )

    fun onRecipeSettingClick(recipeId : Int) = runWithCatch(::onException ) {
        val recipe = recipeMap[recipeId] ?: throw Exception("recipeId not found : $recipeId")
        dialogViewModel = RecipeSettingDialog(
            recipeId = recipeId,
            recipeSettings = recipe.recipeSettings
        )
    }

    fun onCloseDialog(){
        dialogViewModel = ClosedDialogViewModel
    }

    fun onRecipeSettingsChange(recipeId : Int, recipeSettings : RecipeSettings) = launchIo(jobDispatcher, ::onException) {
        val recipe = recipeMap[recipeId]?.copy(
            recipeSettings = recipeSettings
        ) ?: throw Exception("recipeId not found : $recipeId")

        recipeMap[recipeId] = recipe
        recipePreferencesMap[recipeId] = recipeSettings

        dialogViewModel = RecipeSettingDialog(
            recipeId = recipeId,
            recipeSettings = recipe.recipeSettings
        )
        recipeModels.postSwap(recipeMap.values.toList())

    }

    private suspend fun getRecipeIdForItem(itemId : Int, itemRecipePreferenceMap: Map<Int, Int?>) : Int? {
        return if(itemRecipePreferenceMap.containsKey(itemId)) {
            itemRecipePreferenceMap[itemId]
        } else {
            recipeRepo.getRecipesForOutputCached(config.gameId, itemId).firstOrNull()?.id
        }
    }

    fun onRecipeChangeClick(itemId : Int, recipeId : Int) = launchIo(jobDispatcher, ::onException){
        val recipePickerData = RecipePickerData(
            selectedRecipeId = getRecipeIdForItem(itemId, itemRecipePreferenceMap),
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
        dialogViewModel = RecipePickerDialog(
            itemId = itemId,
            recipePickerData = recipePickerData
        )
    }

    fun onRecipePickerSelectedClick(dialogState : RecipePickerDialog, recipeId : Int?) {
        dialogViewModel = dialogState.copy(
            recipePickerData = dialogState.recipePickerData.copy(
                selectedRecipeId = recipeId
            )
        )
    }

    fun onItemRecipeChanged(itemId : Int, recipeId : Int?) = launchIo(jobDispatcher, ::onException){
        itemRecipePreferenceMap[itemId] = recipeId
        dataRefresh()
    }

    data class RecipeSettingDialog(
        val recipeId : Int,
        val recipeSettings : RecipeSettings
    ) : DialogViewModel
    data class RecipePickerDialog(
        val itemId : Int,
        val recipePickerData : RecipePickerData
    ) : DialogViewModel

}

data class ItemInfoModel(
    val id : Int = 0,
    val name : String = "",
    val iconPath : ImageResource? = null,
    val categories : List<String> = listOf()
)

suspend fun Item.toItemInfoModel(appConfig: AppConfig, tokenProvider: TokenProvider) : ItemInfoModel {
    return ItemInfoModel(
        id = this.id,
        name = this.name,
        iconPath = ImageResource.ImageApiResource(
            url = "${appConfig.baseUrl}/images/${this.image}",
            authHeader = tokenProvider.getBearerRefreshToken()
        ),
        categories = this.categories.map {
            it.name
        }
    )
}



data class RecipeViewModel(
    val id : Int,
    val recipeSettings : RecipeSettings = RecipeSettings(
        displayType = RecipeDisplayType.Normal,
        collapseIngredients = true
    ),
    var node : ItemRecipeNode,
    var baseIngredients : List<ItemRecipeNode>,
    var flatRecipeItems : List<ItemRecipeNode>,
    var craftedAt : List<ItemModel> = listOf()
)

data class RecipeSimpleViewModel(
    val id : Int,
    val inputs : List<ItemAmountViewModel>,
    val outputs : List<ItemAmountViewModel>,
    var craftedAt : List<ItemModel> = listOf()
)