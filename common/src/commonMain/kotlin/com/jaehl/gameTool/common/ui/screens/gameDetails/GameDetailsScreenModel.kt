package com.jaehl.gameTool.common.ui.screens.gameDetails

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.ui.screens.home.GameModel
import com.jaehl.gameTool.common.ui.screens.home.toGameModel
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.util.ItemImporter
import kotlinx.coroutines.launch

class GameDetailsScreenModel(
    val jobDispatcher : JobDispatcher,
    val gameRepo: GameRepo,
    val itemImporter : ItemImporter,
    val authProvider: AuthProvider,
    val appConfig: AppConfig
) : ScreenModel {

    private lateinit var config : Config

    var pageLoading = mutableStateOf(false)

    var viewModel = mutableStateOf(GameModel())

    fun setup(config : Config) {
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

            val game = gameRepo.getGame(config.gameId)

            viewModel.value = game.toGameModel(appConfig, authProvider)

            this.pageLoading.value = false
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