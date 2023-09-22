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
import com.jaehl.gameTool.common.data.repo.RecipeRepo
import com.jaehl.gameTool.common.data.service.ImageService
import com.jaehl.gameTool.common.extensions.post
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.extensions.toItemAmountViewModel
import com.jaehl.gameTool.common.extensions.toItemModel
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.screens.runWithCatch
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import com.jaehl.gameTool.common.ui.viewModel.ItemModel
import kotlinx.coroutines.launch
import java.io.File

class ItemEditScreenModel(
    val jobDispatcher : JobDispatcher,
    val itemRepo: ItemRepo,
    val recipeRepo: RecipeRepo,
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
    val items = mutableStateListOf<ItemModel>()

    private val recipeMap : HashMap<Int, RecipeViewModel> = hashMapOf()
    private var recipeIndex = 0

    init {
        itemEditValidator.listener = this
    }

    fun setup(config : Config) {
        this.config = config
        launchIo(jobDispatcher, onException = ::onException) {
                if(config.itemId != null) {
                    loadItem(config.itemId)
                } else {
                    setNewItem()
                }
        }

        launchIo(jobDispatcher, onException = ::onException) {
                itemRepo.getItemCategories(config.gameId).collect {
                    itemCategoriesFullList = it
                    updateItemCategories()
                }
        }
        launchIo(jobDispatcher, onException = ::onException) {
            itemRepo.getItems(config.gameId).collect {
                items.postSwap(it.map { it.toItemModel(appConfig, authProvider) })
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

        recipeRepo.updateIfNotLoaded(config.gameId)

        recipeMap.clear()
        recipeIndex = 0
        recipeRepo.getRecipesForOutput(itemId)
            .map { recipe ->
                RecipeViewModel(
                    id = recipeIndex++,
                    serverID = recipe.id,
                    isDeleted = false,
                    craftingAtList = recipe.craftedAt.map { itemId ->
                        itemRepo.getItem(itemId)?.toItemModel(appConfig, authProvider) ?: throw Exception("item not found : $itemId")
                    },
                    input = recipe.input.map { itemAmount ->
                        itemAmount.toItemAmountViewModel(itemRepo, appConfig, authProvider)
                    },
                    output = recipe.output.map { itemAmount ->
                        itemAmount.toItemAmountViewModel(itemRepo, appConfig, authProvider)
                    }
                )
            }
            .forEach {
                recipeMap[it.id] = it
            }

        viewModel.post(
            ViewModel(
                itemName = TextFieldValue(value = item.name),
                icon = ImageResource.ImageApiResource(
                    url = "${appConfig.baseUrl}/images/${item.image}",
                    authHeader = authProvider.getBearerToken()
                ),
                itemCategories = item.categories,
                allowAddRecipes = true,
                recipeList = recipeMap.values.toList()
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

    private fun updateRecipeFromMap(){
        viewModel.value =
            viewModel.value.copy(
                recipeList = recipeMap.values.toList(),
            )
    }

    fun onAddCreatedAtItem(recipeId : Int, itemId : Int) = runWithCatch(::onException){
        val recipe = recipeMap[recipeId] ?: throw Exception("recipeId not found $recipeId")
        val craftingAtList = recipe.craftingAtList.toMutableList()
        if(craftingAtList.firstOrNull { it.id == itemId } != null) return@runWithCatch
        val item = itemRepo.getItem(itemId)?: throw Exception("itemId not found $itemId")
        craftingAtList.add(item.toItemModel(appConfig, authProvider))

        recipeMap[recipeId] = recipe.copy(
            craftingAtList = craftingAtList
        )
        updateRecipeFromMap()
    }

    fun onDeleteCreatedAtItem(recipeId : Int, itemId : Int) = runWithCatch(::onException){
        val recipe = recipeMap[recipeId] ?: throw Exception("recipeId not found $recipeId")
        val craftingAtList = recipe.craftingAtList.toMutableList()
        val index = craftingAtList.indexOfFirst { it.id == itemId }
        craftingAtList.removeAt(index)
        recipeMap[recipeId] = recipe.copy(
            craftingAtList = craftingAtList
        )
        updateRecipeFromMap()
    }

    private fun updateRecipeItemAmount(recipeId: Int, recipeViewModel: RecipeViewModel, isInput : Boolean, list : List<ItemAmountViewModel>) {
        if(isInput) {
            recipeMap[recipeId] = recipeViewModel.copy(
                input = list
            )
        } else {
            recipeMap[recipeId] = recipeViewModel.copy(
                output = list
            )
        }
        updateRecipeFromMap()
    }

    fun onAddItemAmount(recipeId : Int, isInput : Boolean, itemId : Int) = runWithCatch(::onException){
        val recipe = recipeMap[recipeId] ?: throw Exception("recipeId not found $recipeId")
        val list = (if(isInput) recipe.input else recipe.output).toMutableList()
        val item = itemRepo.getItem(itemId)?: throw Exception("itemId not found $itemId")
        list.add(
            ItemAmountViewModel(
                itemModel = item.toItemModel(appConfig, authProvider),
                amount = 1
            ))
        updateRecipeItemAmount(recipeId, recipe, isInput, list)
    }

    fun onUpdateItemAmountItem(recipeId : Int, isInput : Boolean, oldItemId : Int, newItemId : Int) = runWithCatch(::onException) {
        val recipe = recipeMap[recipeId] ?: throw Exception("recipeId not found $recipeId")
        val list = (if(isInput) recipe.input else recipe.output).toMutableList()
        val index = list.indexOfFirst { it.itemModel.id == oldItemId }
        val item = itemRepo.getItem(newItemId)?: throw Exception("itemId not found $newItemId")
        list[index] = list[index].copy(
            itemModel = item.toItemModel(appConfig, authProvider)
        )
        updateRecipeItemAmount(recipeId, recipe, isInput, list)
    }

    fun onUpdateItemAmountAmount(recipeId : Int, isInput : Boolean, itemId : Int, amount : String) = runWithCatch(::onException) {
        val recipe = recipeMap[recipeId] ?: throw Exception("recipeId not found $recipeId")
        val list = (if(isInput) recipe.input else recipe.output).toMutableList()
        val index = list.indexOfFirst { it.itemModel.id == itemId }
        list[index] = list[index].copy(
            amount = if(amount.isEmpty()) 0 else amount.toInt()
        )
        updateRecipeItemAmount(recipeId, recipe, isInput, list)
    }

    fun onDeleteItemAmount(recipeId : Int, isInput : Boolean, itemId : Int) = runWithCatch(::onException) {
        val recipe = recipeMap[recipeId] ?: throw Exception("recipeId not found $recipeId")
        val list = (if(isInput) recipe.input else recipe.output).toMutableList()
        val index = list.indexOfFirst { it.itemModel.id == itemId }
        list.removeAt(index)
        updateRecipeItemAmount(recipeId, recipe, isInput, list)
    }

    fun onRecipeAdd() = runWithCatch(::onException) {
        val itemId = config.itemId ?: throw Exception("can not add a recipe that has not been added")
        val item = itemRepo.getItem(itemId) ?: throw Exception("item not found $itemId")
        val recipe = RecipeViewModel(
            id = recipeIndex++,
            serverID = null,
            isDeleted = false,
            output = listOf(
                ItemAmountViewModel(
                    itemModel = item.toItemModel(appConfig, authProvider),
                    amount = 1
                ))
            )

        recipeMap[recipe.id] = recipe
        updateRecipeFromMap()
    }



    fun onDeleteRecipe(recipeId : Int) {
        recipeMap.remove(recipeId)
        updateRecipeFromMap()
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
        var id : Int,
        val serverID : Int? = null,
        var isDeleted : Boolean = false,
        var craftingAtList: List<ItemModel> = arrayListOf(),
        var input : List<ItemAmountViewModel> = arrayListOf(),
        var output : List<ItemAmountViewModel> = arrayListOf()
    )
}