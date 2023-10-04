package com.jaehl.gameTool.common.ui.screens.itemDetails

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.RecipeRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.screens.runWithCatch
import com.jaehl.gameTool.common.ui.util.ItemNotFoundException
import com.jaehl.gameTool.common.ui.util.ItemRecipeInverter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import com.jaehl.gameTool.common.ui.viewModel.ItemModel
import com.jaehl.gameTool.common.ui.viewModel.RecipeSettings
import kotlinx.coroutines.launch

class ItemDetailsScreenModel(
    val jobDispatcher : JobDispatcher,
    val tokenProvider: TokenProvider,
    val itemRepo: ItemRepo,
    val recipeRepo: RecipeRepo,
    val appConfig: AppConfig,
    val itemRecipeNodeUtil : ItemRecipeNodeUtil,
    val itemRecipeInverter: ItemRecipeInverter
) : ScreenModel {

    private lateinit var config : Config

    var recipeSettingDialogState = mutableStateOf<RecipeSettingDialogState>(RecipeSettingDialogState.Closed)

    var pageLoading = mutableStateOf<Boolean>(false)
    var itemInfo = mutableStateOf(ItemInfoModel())

    private val recipeMap = hashMapOf<Int, RecipeViewModel>()
    var recipeModels = mutableStateListOf<RecipeViewModel>()

    fun update(config : Config, ifItemChanged : Boolean = false) {
        if(ifItemChanged && this.config.itemId == config.itemId){
            return
        }

        this.config = config
        coroutineScope.launch {
            dataRefresh()
        }
    }

    suspend fun dataRefresh() {
        launchIo(
            jobDispatcher,
            onException = ::onException
        ){
            val item = itemRepo.getItem(config.itemId) ?: throw ItemNotFoundException(config.itemId)

            itemInfo.value = item.toItemInfoModel(appConfig, tokenProvider)

            recipeRepo.preloadRecipes(config.gameId)

            recipeMap.clear()
            recipeRepo.getRecipesForOutput(config.itemId).mapNotNull { recipe ->
                val node = itemRecipeNodeUtil.buildTree(
                    ItemAmountViewModel(
                        itemModel = item.toItemModel(appConfig, tokenProvider),
                        amount = recipe.output.first { it.itemId == config.itemId}.amount
                    ),
                    recipeId = recipe.id
                ) ?: return@mapNotNull null

                val baseIngredients = itemRecipeInverter.invertItemRecipes(listOf(node))
                RecipeViewModel(
                    id = recipe.id,
                    node = node,
                    baseIngredients = baseIngredients,
                    craftedAt = node.recipe?.craftedAt?.mapNotNull {
                        itemRepo.getItem(it)?.toItemModel(appConfig, tokenProvider)
                    } ?: listOf()
                )
            }.forEach {
                recipeMap[it.id] = it
            }
            recipeModels.postSwap(recipeMap.values.toList())

            this.pageLoading.value = false
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
        recipeSettingDialogState.value = RecipeSettingDialogState.Open(
            recipeId = recipeId,
            recipeSettings = recipe.recipeSettings
        )
    }

    fun onRecipeSettingDialogStateClose(){
        recipeSettingDialogState.value = RecipeSettingDialogState.Closed
    }

    fun onRecipeSettingsChange(recipeId : Int, recipeSettings : RecipeSettings) = launchIo(jobDispatcher, ::onException) {
        val recipe = recipeMap[recipeId]?.copy(
            recipeSettings = recipeSettings
        ) ?: throw Exception("recipeId not found : $recipeId")

        recipeMap[recipeId] = recipe

        recipeSettingDialogState.value = RecipeSettingDialogState.Open(
            recipeId = recipeId,
            recipeSettings = recipe.recipeSettings
        )
        recipeModels.postSwap(recipeMap.values.toList())

    }

    sealed class RecipeSettingDialogState{
        data object Closed : RecipeSettingDialogState()
        data class Open(
            val recipeId : Int,
            val recipeSettings : RecipeSettings
        ): RecipeSettingDialogState()
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