package com.jaehl.gameTool.common.ui.screens.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.screens.launchIo

class HomeScreenModel(
    val jobDispatcher : JobDispatcher,
    val gameRepo : GameRepo,
    val tokenProvider: TokenProvider,
    val appConfig: AppConfig
) : ScreenModel {

    var games = mutableStateListOf<GameModel>()
    var pageLoading = mutableStateOf(false)

    fun setup() = launchIo(jobDispatcher, ::onException){
        dataRefresh()
    }

    suspend fun dataRefresh() {
        launchIo(
            jobDispatcher,
            onException = ::onException
        ){
            val games = gameRepo.getGames().map {
                it.toGameModel(appConfig, tokenProvider)
            }
            this.games.postSwap(games)
            this.pageLoading.value = false
        }
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

}