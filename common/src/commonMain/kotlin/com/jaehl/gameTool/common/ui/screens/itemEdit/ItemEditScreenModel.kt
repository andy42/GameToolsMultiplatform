package com.jaehl.gameTool.common.ui.screens.itemEdit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.model.Item
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.service.ImageService
import com.jaehl.gameTool.common.extensions.post
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import com.jaehl.gameTool.common.ui.viewModel.ItemModel
import kotlinx.coroutines.launch
import java.io.File

class ItemEditScreenModel(
    val jobDispatcher : JobDispatcher,
    val itemRepo: ItemRepo,
    val imageService : ImageService,
    val appConfig : AppConfig,
    val authProvider: AuthProvider,
    val itemEditValidator : ItemEditValidator
) : ScreenModel, ItemEditValidator.Listener{

    private lateinit var config : Config

    var title = mutableStateOf("")
    var pageLoading = mutableStateOf<Boolean>(false)

    val viewModel = mutableStateOf(ViewModel())
    private var item : Item? = null

    private var itemCategoriesFullList = listOf<ItemCategory>()
    val itemCategories = mutableStateListOf<ItemCategory>()

    init {
        itemEditValidator.listener = this
    }

    fun setup(config : Config) {
        this.config = config
        launchIo(
            jobDispatcher,
            onException = ::onException) {

                if(config.itemId != null) {
                    loadItem(config.itemId)
                } else {
                    setNewItem()
                }
        }

        launchIo(
            jobDispatcher,
            onException = ::onException) {
                itemRepo.getItemCategories(config.gameId).collect {
                    itemCategoriesFullList = it
                    updateItemCategories()
                }
        }
    }

    suspend fun updateItemCategories(){
        itemCategories.postSwap(
            itemCategoriesFullList.filter {
                !viewModel.value.itemCategories.contains(it)
            }
        )
    }

    suspend fun loadItem(itemId : Int) {
        title.value = "Update Item"
        val item = itemRepo.getItem(itemId) ?: throw Exception("loadItem Item not found : $itemId")
        this.item = item
        viewModel.post(
            ViewModel(
                itemName = TextFieldValue(value = item.name),
                icon = ImageResource.ImageApiResource(
                    url = "${appConfig.baseUrl}/images/${item.image}",
                    authHeader = authProvider.getBearerToken()
                ),
                itemCategories = item.categories,
                allowAddRecipes = true
            )
        )
        updateItemCategories()
        this.pageLoading.value = false
    }

    suspend fun setNewItem(){
        title.value = "Add New Item"
        viewModel.post(
            ViewModel(
                itemName = TextFieldValue(value = ""),
                itemCategories = listOf(),
                allowAddRecipes = false
            )
        )
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

    fun onItemNameChange(value : String) {
        viewModel.value =
            viewModel.value.copy(
                itemName = TextFieldValue(value = value),
            )
    }

    fun onIconChange(filePath : String) {
        viewModel.value =
            viewModel.value.copy(
                icon = ImageResource.ImageLocalResource(filePath),
                iconError = ""
            )
    }

    fun onItemCategoryAdd(itemCategory: ItemCategory) {
        val itemCategories = viewModel.value.itemCategories.toMutableList()
        if(!itemCategories.contains(itemCategory)){
            itemCategories.add(itemCategory)
        }

        coroutineScope.launch {
            viewModel.value =
                viewModel.value.copy(
                    itemCategories = itemCategories
                )
            updateItemCategories()
        }
    }

    private suspend fun saveNewItem(){

        if(!itemEditValidator.validate(
            name = viewModel.value.itemName.value,
            image = viewModel.value.icon
        )){
            return
        }

        val imageFile = File((viewModel.value.icon as ImageResource.ImageLocalResource).url)
        val image = imageService.addImage(
            imageFile = imageFile,
            description = viewModel.value.itemName.value
        )

        val newItem = itemRepo.addItem(
            game = config.gameId,
            name = viewModel.value.itemName.value,
            categories = viewModel.value.itemCategories.map { it.id },
            image = image.imageId
        )

        this.config = Config(gameId = config.gameId, itemId = newItem.id)
        loadItem(newItem.id)
    }

    private suspend fun updateItem(){

        if(!itemEditValidator.validate(
                name = viewModel.value.itemName.value,
                image = viewModel.value.icon
            )){
            return
        }

        var imageId = item?.image ?: -1

        if(viewModel.value.icon is ImageResource.ImageLocalResource) {
            val imageFile = File((viewModel.value.icon as ImageResource.ImageLocalResource).url)
            imageId = imageService.addImage(
                imageFile = imageFile,
                description = viewModel.value.itemName.value
            ).imageId
        }

        val item = itemRepo.updateItem(
            game = config.gameId,
            name = viewModel.value.itemName.value,
            categories = viewModel.value.itemCategories.map { it.id },
            image = imageId
        )
        loadItem(item.id)
    }

    fun save() = launchIo(jobDispatcher, ::onException){
        if(config.itemId == null){
            saveNewItem()
        } else {
            updateItem()
        }
    }

    override fun onNameError(error: String) {
        viewModel.value =
            viewModel.value.copy(
                itemName = viewModel.value.itemName.copy(
                    error = error
                ),
            )
    }

    override fun onImageError(error: String) {
        viewModel.value =
            viewModel.value.copy(
                iconError = error
            )
    }

    fun onItemCategoryDelete(itemCategory: ItemCategory) {
        val itemCategories = viewModel.value.itemCategories.toMutableList()
        itemCategories.remove(itemCategory)
        coroutineScope.launch {
            viewModel.value =
                viewModel.value.copy(
                    itemCategories = itemCategories
                )
            updateItemCategories()
        }
    }

    data class Config(
        val gameId : Int,
        val itemId : Int?
    )

    data class ViewModel(
        val itemName : TextFieldValue = TextFieldValue(),
        val icon : ImageResource = ImageResource.ImageLocalResource(""),
        val iconError : String = "",
        val itemCategories : List<ItemCategory> = listOf(),
        val recipeList : List<RecipeViewModel> = listOf(),
        val allowAddRecipes : Boolean = false
    )

    data class RecipeViewModel(
        var id : String = "",
        var name : String = "",
        var isDeleted : Boolean = false,
        var craftingAtList: ArrayList<ItemModel> = arrayListOf(),
        var input : ArrayList<ItemAmountViewModel> = arrayListOf(),
        var output : ArrayList<ItemAmountViewModel> = arrayListOf()
    )
}