package com.jaehl.gameTool.common.ui.screens.itemDetails

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.viewModel.ItemModel
import kotlinx.coroutines.launch

class ItemDetailsScreenModel(
    val jobDispatcher : JobDispatcher,
    val authProvider: AuthProvider,
    val config : Config,
    val itemRepo: ItemRepo
) : ScreenModel {

    var pageLoading = mutableStateOf<Boolean>(false)
    var itemInfo = mutableStateOf(ItemInfoModel())
    var recipes = mutableStateListOf<RecipeViewModel>()

    init {
        coroutineScope.launch {
            dataRefresh()
        }
    }

    suspend fun dataRefresh() {
        launchIo(
            jobDispatcher,
            onException = ::onException
        ){
            itemRepo.getItem(config.itemId).collect{ item ->
                itemInfo.value = item.toItemInfoModel(authProvider)
            }


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

fun Item.toItemInfoModel(authProvider: AuthProvider) : ItemInfoModel {
    return ItemInfoModel(
        id = this.id,
        name = this.name,
        iconPath = ImageResource.ImageApiResource(
            url = "http://0.0.0.0:8080/images/${this.image}",
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