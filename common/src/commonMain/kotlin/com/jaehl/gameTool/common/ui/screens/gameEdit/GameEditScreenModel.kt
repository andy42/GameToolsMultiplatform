package com.jaehl.gameTool.common.ui.screens.gameEdit

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.ui.componets.TextFieldValue
import com.jaehl.gameTool.common.ui.screens.launchIo

class GameEditScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val gameRepo: GameRepo,
    private val gameEditValidator : GameEditValidator
) : ScreenModel, GameEditValidator.ValidatorListener {

    private lateinit var config : Config

    val viewModel = mutableStateOf(GameEditViewModel())
    val closePageEvent = mutableStateOf(false)

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
        viewModel.value = viewModel.value.copy(
            name = TextFieldValue(value = game.name),
            showDelete = true
        )
    }

    private fun onException(t : Throwable) {
        System.err.println(t.message)
    }

    fun onNameChange(value : String) {
        viewModel.value = viewModel.value.copy(
            name = TextFieldValue(value = value)
        )
    }

    fun onSaveClick() = launchIo(jobDispatcher, ::onException) {
        val viewModel = viewModel.value
        if(!gameEditValidator.validate(viewModel.name.value)) return@launchIo

        val gameId = config.gameId

        if(gameId == null) {
            config = Config(
                gameId = gameRepo.createGame(viewModel.name.value).id
            )
        } else {
            config = Config(
                gameId = gameRepo.updateGame(gameId, viewModel.name.value).id
            )
        }
        config.gameId?.let { gameId ->
            updateGame(gameId)
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

    data class Config(
        val gameId : Int?
    )
}


data class GameEditViewModel(
    val pageLoading : Boolean = false,
    val showDelete : Boolean = false,
    val name : TextFieldValue = TextFieldValue()
)