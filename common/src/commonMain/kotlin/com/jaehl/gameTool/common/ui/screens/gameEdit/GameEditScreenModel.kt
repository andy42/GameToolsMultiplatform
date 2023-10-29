package com.jaehl.gameTool.common.ui.screens.gameEdit

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.model.ImageType
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.ImageService
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.launchIo
import java.io.File

class GameEditScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val itemRepo : ItemRepo,
    private val gameRepo: GameRepo,
    private val imageService : ImageService,
    val appConfig : AppConfig,
    val tokenProvider: TokenProvider,
    private val gameEditValidator : GameEditValidator
) : ScreenModel, GameEditValidator.ValidatorListener {

    private lateinit var config : Config

    val viewModel = mutableStateOf(GameEditViewModel())
    val closePageEvent = mutableStateOf(false)

    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.Closed)

    private var newItemCategoryIndex = -1
    private var newItemCategories = arrayListOf<ItemCategory>()

    private var itemCategoriesFullList = listOf<ItemCategory>()


    private var game : Game? = null
    private var unsavedChanges = false

    init {
        gameEditValidator.validatorListener = this
    }

    fun setup(config : Config) {
        this.config = config

        newItemCategoryIndex = -1
        newItemCategories.clear()

        launchIo(jobDispatcher, ::onException) {


            if(config.gameId != null){
                updateGame(config.gameId)
            }
        }

        launchIo(jobDispatcher, onException = ::onException) {
            itemRepo.getItemCategories(config.gameId).collect {
                itemCategoriesFullList = it
            }
        }
    }

    suspend fun updateGame(gameId : Int){
        val game = gameRepo.getGame(gameId)
        this.game = game

        viewModel.value = viewModel.value.copy(
            name = TextFieldValue(value = game.name),
            showDelete = true,
            itemCategories = game.itemCategories,
            icon = ImageResource.ImageApiResource(
                url = "${appConfig.baseUrl}/images/${game.icon}",
                authHeader = tokenProvider.getBearerRefreshToken()
            ),
            banner = ImageResource.ImageApiResource(
                url = "${appConfig.baseUrl}/images/${game.banner}",
                authHeader = tokenProvider.getBearerRefreshToken()
            ),
        )
    }

    private fun onException(t : Throwable) {
        System.err.println(t.message)
    }

    fun onNameChange(value : String) {
        viewModel.value = viewModel.value.copy(
            name = TextFieldValue(value = value)
        )
        unsavedChanges = true
    }

    fun onIconChange(filePath : String) {
        viewModel.value =
            viewModel.value.copy(
                icon = ImageResource.ImageLocalResource(filePath),
                iconError = ""
            )
        unsavedChanges = true
    }

    fun onBannerChange(filePath : String) {
        viewModel.value =
            viewModel.value.copy(
                banner = ImageResource.ImageLocalResource(filePath),
                bannerError = ""
            )
        unsavedChanges = true
    }

    fun onSaveClick(closePageAfter : Boolean = false) = launchIo(jobDispatcher, ::onException) {
        val viewModel = viewModel.value
        if(!gameEditValidator.validate(viewModel.name.value, viewModel.icon, viewModel.banner)) return@launchIo

        val gameId = config.gameId

        var iconImage = game?.icon ?: -1
        if(viewModel.icon is ImageResource.ImageLocalResource) {
            val imageType = ImageType.fromFileExtension( viewModel.icon.getFileExtension() )
            val imageFile = File(viewModel.icon.url)
            iconImage = imageService.addImage(
                imageFile = imageFile,
                imageType = imageType,
                description = "${viewModel.name.value}_icon"
            ).id
        }

        var bannerImage = game?.banner ?: -1
        if(viewModel.banner is ImageResource.ImageLocalResource) {
            val imageType = ImageType.fromFileExtension(viewModel.banner.getFileExtension())
            val imageFile = File(viewModel.banner.url)
            bannerImage = imageService.addImage(
                imageFile = imageFile,
                imageType = imageType,
                description = "${viewModel.name.value}_icon"
            ).id
        }

        val itemCategories = viewModel.itemCategories.toMutableList()
        viewModel.itemCategories.forEachIndexed { index, itemCategory ->
            if(itemCategory.id > 0) return@forEachIndexed
            val newItemCategory = itemRepo.addItemCategory(itemCategory.name)
            itemCategories[index] = newItemCategory
        }
        this.viewModel.value = viewModel.copy(
            itemCategories = itemCategories
        )

        if(gameId == null) {
            config = Config(
                gameId = gameRepo.createGame(
                    name = viewModel.name.value,
                    itemCategories = itemCategories.map { it.id },
                    icon = iconImage,
                    banner = bannerImage).id
            )
        } else {
            config = Config(
                gameId = gameRepo.updateGame(
                    gameId,
                    name = viewModel.name.value,
                    itemCategories = itemCategories.map { it.id },
                    icon = iconImage,
                    banner = bannerImage).id
            )
        }
        config.gameId?.let { gameId ->
            updateGame(gameId)
        }
        unsavedChanges = false
        if(closePageAfter){
            closePageEvent.value = true
        }
    }

    fun onBackClick() {
        if(unsavedChanges){
            openDialogSaveWarning()
        } else {
            closePageEvent.value = true
        }
    }

    fun onDelete() = launchIo(jobDispatcher, ::onException) {
        config.gameId?.let { gameId ->
            gameRepo.delete(gameId)
        }
        closePageEvent.value = true
    }

    fun closeDialog() {
        dialogConfig.value = DialogConfig.Closed
    }

    fun addNewItemCategory(name : String) {
        val dialogItemCategoryPicker = dialogConfig.value as? DialogConfig.DialogItemCategoryPicker ?: return
        if(name.isEmpty()) {
            dialogConfig.value = dialogItemCategoryPicker.copy(
                addError = "new Category can not be empty"
            )
            return
        }
        (newItemCategories+itemCategoriesFullList).forEach {
            dialogConfig.value = dialogItemCategoryPicker.copy(
                addError = "new Category already exists"
            )
            if(it.name == name){
                return@addNewItemCategory
            }
        }
        val newItemCategory = ItemCategory(
            id = newItemCategoryIndex--,
            name = name
        )
        newItemCategories.add(newItemCategory)
        onItemCategoryAdd(newItemCategory)
        unsavedChanges = true
    }

    fun openDialogItemCategoryPicker() = launchIo(jobDispatcher, ::onException) {
        dialogConfig.value = DialogConfig.DialogItemCategoryPicker(
            itemCategories = (newItemCategories+itemCategoriesFullList)
                .filter {
                    !viewModel.value.itemCategories.contains(it)
                }
        )
    }

    fun onDialogItemCategoryPickerSearchTextChange(searchText : String) {
        val dialogItemCategoryPicker = dialogConfig.value as? DialogConfig.DialogItemCategoryPicker ?: return
        dialogConfig.value = dialogItemCategoryPicker.copy(
            searchText = searchText,
            addError = ""
        )
    }

    fun openDialogSaveWarning() = launchIo(jobDispatcher, ::onException) {
        dialogConfig.value = DialogConfig.DialogSaveWarning
    }

    fun onItemCategoryAdd(itemCategory: ItemCategory) {
        val viewModel = viewModel.value
        val itemCategories = viewModel.itemCategories.toMutableList()
        itemCategories.add(itemCategory)
        this.viewModel.value = viewModel.copy(
            itemCategories = itemCategories
        )
        unsavedChanges = true
    }

    fun onItemCategoryDelete(itemCategory: ItemCategory) {
        val viewModel = viewModel.value
        val itemCategories = viewModel.itemCategories.toMutableList()
        itemCategories.remove(itemCategory)
        this.viewModel.value = viewModel.copy(
            itemCategories = itemCategories
        )
        unsavedChanges = true
    }

    override fun onNameError(error: String) {
        viewModel.value = viewModel.value.copy(
            name = viewModel.value.name.copy(
                error = error
            )
        )
    }

    override fun onIconError(error: String) {
        viewModel.value = viewModel.value.copy(
            iconError = error
        )
    }

    override fun onBannerError(error: String) {
        viewModel.value = viewModel.value.copy(
            bannerError = error
        )
    }

    data class Config(
        val gameId : Int?
    )

    sealed class DialogConfig {
        data object Closed : DialogConfig()
        data class DialogItemCategoryPicker(
            val itemCategories : List<ItemCategory>,
            val searchText : String = "",
            val addError : String = ""
        ) : DialogConfig()
        data object DialogSaveWarning : DialogConfig()
    }
}


data class GameEditViewModel(
    val pageLoading : Boolean = false,
    val showDelete : Boolean = false,
    val name : TextFieldValue = TextFieldValue(),
    val itemCategories: List<ItemCategory> = listOf(),
    val icon : ImageResource = ImageResource.ImageLocalResource(""),
    val iconError : String = "",
    val banner : ImageResource = ImageResource.ImageLocalResource(""),
    val bannerError : String = "",
)