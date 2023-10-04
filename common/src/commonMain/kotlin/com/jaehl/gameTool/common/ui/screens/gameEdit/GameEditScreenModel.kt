package com.jaehl.gameTool.common.ui.screens.gameEdit

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.model.ImageType
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.ImageService
import com.jaehl.gameTool.common.ui.componets.ImageResource
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.launchIo
import java.io.File

class GameEditScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val gameRepo: GameRepo,
    private val imageService : ImageService,
    val appConfig : AppConfig,
    val tokenProvider: TokenProvider,
    private val gameEditValidator : GameEditValidator
) : ScreenModel, GameEditValidator.ValidatorListener {

    private lateinit var config : Config

    val viewModel = mutableStateOf(GameEditViewModel())
    val showExitSaveDialog = mutableStateOf(false)
    val closePageEvent = mutableStateOf(false)
    private var game : Game? = null
    private var unsavedChanges = false

    init {
        gameEditValidator.validatorListener = this
    }

    fun setup(config : Config) {
        this.config = config

        launchIo(jobDispatcher, ::onException) {
            if(config.gameId != null){
                updateGame(config.gameId)
            }
        }
    }

    suspend fun updateGame(gameId : Int){
        val game = gameRepo.getGame(gameId)
        this.game = game
        viewModel.value = viewModel.value.copy(
            name = TextFieldValue(value = game.name),
            showDelete = true,
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

    fun onSaveClick() = launchIo(jobDispatcher, ::onException) {
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
            ).imageId
        }

        var bannerImage = game?.banner ?: -1
        if(viewModel.banner is ImageResource.ImageLocalResource) {
            val imageType = ImageType.fromFileExtension(viewModel.banner.getFileExtension())
            val imageFile = File(viewModel.banner.url)
            bannerImage = imageService.addImage(
                imageFile = imageFile,
                imageType = imageType,
                description = "${viewModel.name.value}_icon"
            ).imageId
        }


        if(gameId == null) {
            config = Config(
                gameId = gameRepo.createGame(viewModel.name.value, icon = iconImage, banner = bannerImage).id
            )
        } else {
            config = Config(
                gameId = gameRepo.updateGame(gameId, viewModel.name.value, icon = iconImage, banner = bannerImage).id
            )
        }
        config.gameId?.let { gameId ->
            updateGame(gameId)
        }
        unsavedChanges = false
    }

    fun onBackClick() {
        if(unsavedChanges){
            showExitSaveDialog.value = true
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
}


data class GameEditViewModel(
    val pageLoading : Boolean = false,
    val showDelete : Boolean = false,
    val name : TextFieldValue = TextFieldValue(),
    val icon : ImageResource = ImageResource.ImageLocalResource(""),
    val iconError : String = "",
    val banner : ImageResource = ImageResource.ImageLocalResource(""),
    val bannerError : String = "",
)