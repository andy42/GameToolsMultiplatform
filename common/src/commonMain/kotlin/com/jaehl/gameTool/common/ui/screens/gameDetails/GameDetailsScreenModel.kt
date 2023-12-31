package com.jaehl.gameTool.common.ui.screens.gameDetails

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.ui.screens.home.GameModel
import com.jaehl.gameTool.common.ui.screens.home.toGameModel
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.util.ItemImporter

class GameDetailsScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val gameRepo: GameRepo,
    private val itemImporter : ItemImporter,
    private val tokenProvider: TokenProvider,
    private val appConfig: AppConfig
) : ScreenModel {

    private lateinit var config : Config

    var pageLoading = mutableStateOf(false)

    var viewModel = mutableStateOf(GameModel())

    fun setup(config : Config) {
        this.config = config
        launchIo(jobDispatcher, ::onException){
            dataRefresh()
        }
    }

    suspend fun dataRefresh() {
        launchIo(
            jobDispatcher,
            onException = ::onException
        ){
            gameRepo.getGameFlow(config.gameId).collect { gameResource ->
                pageLoading.value = (gameResource is Resource.Loading)
                if(gameResource is Resource.Error){
                    onException(gameResource.exception)
                    return@collect
                }
                viewModel.value = gameResource.getDataOrThrow().toGameModel(appConfig, tokenProvider)

            }
        }
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

    data class Config(
        val gameId : Int
    )

    fun testImport() = launchIo(
        jobDispatcher,
        onException = ::onException
    ) {
        itemImporter.import(config.gameId)
    }
}