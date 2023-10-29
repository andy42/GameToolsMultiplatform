package com.jaehl.gameTool.common.ui.screens.itemList

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.componets.ImageResource
import kotlinx.coroutines.launch

class ItemListScreenModel(
    val jobDispatcher : JobDispatcher,
    val tokenProvider: TokenProvider,
    val appConfig: AppConfig,
    val itemRepo: ItemRepo,
    val userRepo: UserRepo,
    val gameRepo: GameRepo
) : ScreenModel {

    private lateinit var config : Config

    var showEditItems = mutableStateOf(false)

    var pageLoading = mutableStateOf<Boolean>(false)
        private set

    var items = mutableStateListOf<ItemRowModel>()
        private set

    val searchText =  mutableStateOf("")
    val categoryFilter = mutableStateOf(ItemCategory.Item_Category_ALL)

    private var itemCategories = listOf<ItemCategory>()

    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.Closed)

    fun setup(config : Config) {
        this.config = config
        coroutineScope.launch {
            dataRefresh()
        }
    }

    suspend fun dataRefresh() {
        launchIo(jobDispatcher, ::onException){
            userRepo.getUserSelf().let { user ->
                showEditItems.value = listOf(
                    User.Role.Admin,
                    User.Role.Contributor
                ).contains(user.role)
            }
        }
        launchIo(
            jobDispatcher,
            onException = ::onException
        ){
            itemRepo.getItemsFlow(config.gameId).collect { newItems ->
                this.items.postSwap(
                    newItems.map {item ->
                        item.toItemRowModel(appConfig, tokenProvider)
                    }
                )
            }
            this.pageLoading.value = false
        }
        launchIo(
            jobDispatcher,
            onException = ::onException) {

            val list = mutableListOf(ItemCategory.Item_Category_ALL)
            list.addAll(gameRepo.getGame(config.gameId).itemCategories)
            itemCategories = list
        }
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

    fun openDialogItemCategoryPicker() = launchIo(jobDispatcher, ::onException) {
        dialogConfig.value = DialogConfig.DialogItemCategoryPicker(
            itemCategories = itemCategories
        )
    }

    fun onDialogItemCategoryPickerSearchTextChange(searchText : String) {
        val dialogItemCategoryPicker = dialogConfig.value as? DialogConfig.DialogItemCategoryPicker ?: return
        dialogConfig.value = dialogItemCategoryPicker.copy(
            searchText = searchText
        )
    }

    fun closeDialog() {
        dialogConfig.value = DialogConfig.Closed
    }

    data class Config(
        val gameId : Int
    )

    sealed class DialogConfig {
        data object Closed : DialogConfig()
        data class DialogItemCategoryPicker(
            val itemCategories : List<ItemCategory>,
            val searchText : String = ""
        ) : DialogConfig()
    }
}

suspend fun Item.toItemRowModel(appConfig: AppConfig, tokenProvider: TokenProvider) : ItemRowModel {
    return ItemRowModel(
        id = this.id,
        name = this.name,
        itemCategories = this.categories,
        imageResource = ImageResource.ImageApiResource(
            url = "${appConfig.baseUrl}/images/${this.image}",
            authHeader = tokenProvider.getBearerRefreshToken()
        )
    )
}