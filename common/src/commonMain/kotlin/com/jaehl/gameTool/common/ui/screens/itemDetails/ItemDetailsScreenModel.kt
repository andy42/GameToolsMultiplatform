package com.jaehl.gameTool.common.ui.screens.itemDetails

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.RecipeRepo
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.util.ItemNotFoundException
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import com.jaehl.gameTool.common.ui.viewModel.ItemModel
import kotlinx.coroutines.launch

class ItemDetailsScreenModel(
    val jobDispatcher : JobDispatcher,
    val authProvider: AuthProvider,
    var config : Config,
    val itemRepo: ItemRepo,
    val recipeRepo: RecipeRepo,
    val appConfig: AppConfig,
    val itemRecipeNodeUtil : ItemRecipeNodeUtil
) : ScreenModel {

    var pageLoading = mutableStateOf<Boolean>(false)
    var itemInfo = mutableStateOf(ItemInfoModel())
    var recipeModels = mutableStateListOf<RecipeViewModel>()

    fun update(config : Config) {
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

            itemInfo.value = item.toItemInfoModel(appConfig, authProvider)

            recipeRepo.updateIfNotLoaded(config.gameId)

            val recipes = recipeRepo.getRecipesForOutput(config.itemId).mapNotNull { recipe ->
                val node = itemRecipeNodeUtil.buildTree(
                    ItemAmountViewModel(
                        item = item.toItemModel(appConfig, authProvider),
                        amount = recipe.output.first { it.itemId == config.itemId}.amount
                    ),
                    recipeId = recipe.id
                ) ?: return@mapNotNull null
                RecipeViewModel(
                    node = node,
                    craftedAt = node.recipe?.craftedAt?.mapNotNull {
                        itemRepo.getItem(it)?.toItemModel(appConfig, authProvider)
                    } ?: listOf()
                )
            }
            recipeModels.postSwap(recipes)

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
}

data class ItemInfoModel(
    val id : Int = 0,
    val name : String = "",
    val iconPath : ImageResource? = null,
    val categories : List<String> = listOf()
)

fun Item.toItemInfoModel(appConfig: AppConfig, authProvider: AuthProvider) : ItemInfoModel {
    return ItemInfoModel(
        id = this.id,
        name = this.name,
        iconPath = ImageResource.ImageApiResource(
            url = "${appConfig.baseUrl}/images/${this.image}",
            authHeader = authProvider.getBearerToken()
        ),
        categories = this.categories.map {
            it.name
        }
    )
}

data class RecipeViewModel(
    var node : ItemRecipeNode,
    var craftedAt : List<ItemModel> = listOf(),
    var collapseList : Boolean = true,
    var showBaseCrafting : Boolean = false
)