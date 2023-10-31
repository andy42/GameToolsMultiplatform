package com.jaehl.gameTool.common.ui.screens.itemDetails

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.componets.RecipePickerData
import com.jaehl.gameTool.common.ui.screens.runWithCatch
import com.jaehl.gameTool.common.ui.util.ItemRecipeInverter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import com.jaehl.gameTool.common.ui.viewModel.ItemModel
import com.jaehl.gameTool.common.ui.viewModel.RecipeSettings
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

class ItemDetailsScreenModel(
    val jobDispatcher : JobDispatcher,
    val tokenProvider: TokenProvider,
    val itemRepo: ItemRepo,
    val userRepo: UserRepo,
    val recipeRepo: RecipeRepo,
    val appConfig: AppConfig,
    val itemRecipeNodeUtil : ItemRecipeNodeUtil,
    val itemRecipeInverter: ItemRecipeInverter
) : ScreenModel {

    private lateinit var config : Config

    var showEditItems = mutableStateOf(false)

    var dialogState = mutableStateOf<DialogState>(DialogState.Closed)

    var pageLoading = mutableStateOf<Boolean>(false)
    var itemInfo = mutableStateOf(ItemInfoModel())

    private val itemRecipePreferenceMap =  hashMapOf<Int, Int?>()

    private val recipeMap = hashMapOf<Int, RecipeViewModel>()
    var recipeModels = mutableStateListOf<RecipeViewModel>()

    private val recipePreferencesMap = hashMapOf<Int, RecipeSettings>()

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


        pageLoading.value = (userResource is Resource.Loading || itemResource is Resource.Loading || recipesResource is Resource.Loading)

        listOf<Resource<*>>(userResource, itemResource, recipesResource).forEach {
            if(it is Resource.Error){
                onException(it.exception)
                return
            }
        }

        val recipeOutputMap = LinkedHashMap<Int, ArrayList<Recipe>>()
        recipesResource.getDataOrThrow().forEach { recipe ->
            recipe.output.forEach {itemAmount ->
                recipeOutputMap[itemAmount.itemId] = updateRecipeOutputArray(recipeOutputMap[itemAmount.itemId], recipe)
            }
        }

        showEditItems.value = listOf(
            User.Role.Admin,
            User.Role.Contributor
        ).contains(userResource.getDataOrThrow().role)

        val itemMap = HashMap<Int, Item>()
        itemResource.getDataOrThrow().forEach { item ->
            itemMap[item.id] = item
        }
        val item = itemMap[config.itemId] ?: throw UiException.NotFound("item not found : ${config.itemId}")

        itemInfo.value = item.toItemInfoModel(appConfig, tokenProvider)

        val recipes = recipeOutputMap[item.id] ?: listOf()
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
                    showBaseIngredients = false,
                    collapseIngredients = true
                ),
                baseIngredients = baseIngredients,
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
                itemRepo.getItemsFlow(config.gameId),
                recipeRepo.getRecipesFlow(config.gameId)
            ) { userResource : Resource<User>, itemResource : Resource<List<Item>>, recipesResource : Resource<List<Recipe>> ->

                updateUi(userResource, itemResource, recipesResource)
            }.collect()
        }
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

    data class Config(
        val gameId : Int,
        val itemId : Int
    )

    fun onRecipeSettingClick(recipeId : Int) = runWithCatch(::onException ) {
        val recipe = recipeMap[recipeId] ?: throw Exception("recipeId not found : $recipeId")
        dialogState.value = DialogState.RecipeSettingDialog(
            recipeId = recipeId,
            recipeSettings = recipe.recipeSettings
        )
    }

    fun onCloseDialog(){
        dialogState.value = DialogState.Closed
    }

    fun onRecipeSettingsChange(recipeId : Int, recipeSettings : RecipeSettings) = launchIo(jobDispatcher, ::onException) {
        val recipe = recipeMap[recipeId]?.copy(
            recipeSettings = recipeSettings
        ) ?: throw Exception("recipeId not found : $recipeId")

        recipeMap[recipeId] = recipe
        recipePreferencesMap[recipeId] = recipeSettings

        dialogState.value = DialogState.RecipeSettingDialog(
            recipeId = recipeId,
            recipeSettings = recipe.recipeSettings
        )
        recipeModels.postSwap(recipeMap.values.toList())

    }

    private suspend fun getRecipeIdForItem(itemId : Int, itemRecipePreferenceMap: Map<Int, Int?>) : Int? {
        if(itemRecipePreferenceMap.containsKey(itemId)) {
            return itemRecipePreferenceMap[itemId]
        } else {
            return recipeRepo.getRecipesForOutput(itemId).firstOrNull()?.id
        }
    }

    fun onRecipeChangeClick(itemId : Int, recipeId : Int) = launchIo(jobDispatcher, ::onException){
        val recipePickerData = RecipePickerData(
            selectedRecipeId = getRecipeIdForItem(itemId, itemRecipePreferenceMap),
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

    fun onItemRecipeChanged(itemId : Int, recipeId : Int?) = launchIo(jobDispatcher, ::onException){
        itemRecipePreferenceMap[itemId] = recipeId
        dataRefresh()
    }

    sealed class DialogState{
        data object Closed : DialogState()
        data class RecipeSettingDialog(
            val recipeId : Int,
            val recipeSettings : RecipeSettings
        ): DialogState()
        data class RecipePickerDialog(
            val itemId : Int,
            val recipePickerData : RecipePickerData
        ) : DialogState()
    }

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
        showBaseIngredients = false,
        collapseIngredients = true
    ),
    var node : ItemRecipeNode,
    var baseIngredients : List<ItemRecipeNode>,
    var craftedAt : List<ItemModel> = listOf()
)