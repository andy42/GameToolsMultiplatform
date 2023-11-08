package com.jaehl.gameTool.common.ui.screens.itemList

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.viewModel.ClosedDialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.ItemCategoryPickerDialogViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ItemListScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val tokenProvider: TokenProvider,
    private val appConfig: AppConfig,
    private val itemRepo: ItemRepo,
    private val userRepo: UserRepo,
    private val gameRepo: GameRepo,
    private val uiExceptionHandler : UiExceptionHandler
) : ScreenModel {

    private lateinit var config : Config

    var showEditItems = mutableStateOf(false)

    var pageLoading = mutableStateOf<Boolean>(false)
        private set

    var items = mutableStateListOf<ItemRowModel>()
        private set

    val searchText =  mutableStateOf("")
    val categoryFilter = mutableStateOf(ItemCategory.Item_Category_ALL)

    val itemCategories = mutableStateListOf<ItemCategory>()

    val dialogViewModel = mutableStateOf<DialogViewModel>(ClosedDialogViewModel)

    fun setup(config : Config) {
        this.config = config
        coroutineScope.launch {
            dataRefresh()
        }
    }

    private suspend fun updateUi(
        userResource : Resource<User>,
        itemsResource : Resource<List<Item>>,
        itemCategoriesResource : Resource<List<ItemCategory>>){

        pageLoading.value = (userResource is Resource.Loading
                || itemsResource is Resource.Loading
                || itemCategoriesResource is Resource.Loading)


        listOf<Resource<*>>(userResource, itemsResource, itemCategoriesResource).forEach {
            if(it is Resource.Error){
                onException(it.exception)
                return
            }
        }

        userResource.getDataOrThrow().let { user ->
            showEditItems.value = listOf(
                User.Role.Admin,
                User.Role.Contributor
            ).contains(user.role)
        }

        this.items.postSwap(
            itemsResource.getDataOrThrow().map { item ->
                item.toItemRowModel(appConfig, tokenProvider)
            }
        )

        val list = mutableListOf(ItemCategory.Item_Category_ALL)
        list.addAll(itemCategoriesResource.getDataOrThrow())
        itemCategories.postSwap(list)
    }

    suspend fun dataRefresh() {

        pageLoading.value = true
        combine(
            userRepo.getUserSelFlow(),
            itemRepo.getItems(config.gameId),
            gameRepo.getGameItemCategories(config.gameId)
        ) { userResource, itemsResource, itemCategoriesResource ->
            updateUi(
                userResource,
                itemsResource,
                itemCategoriesResource
            )
        }.collect()
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)

        dialogViewModel.value = uiExceptionHandler.handelException(t)
    }

    fun openDialogItemCategoryPicker() = launchIo(jobDispatcher, ::onException) {
        dialogViewModel.value = ItemCategoryPickerDialogViewModel()
    }

    fun onDialogItemCategoryPickerSearchTextChange(searchText : String) {
        val dialogItemCategoryPicker = dialogViewModel.value as? ItemCategoryPickerDialogViewModel ?: return
        dialogViewModel.value = dialogItemCategoryPicker.copy(
            searchText = searchText
        )
    }

    fun closeDialog() {
        dialogViewModel.value = ClosedDialogViewModel
    }

    data class Config(
        val gameId : Int
    )

    sealed class DialogConfig {
        data object Closed : DialogConfig()
        data class DialogItemCategoryPicker(
            val searchText : String = ""
        ) : DialogConfig()
        data class ErrorDialog(
            val title : String,
            val message : String
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