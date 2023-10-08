package com.jaehl.gameTool.common.ui.screens.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.util.ServerBackup

class HomeScreenModel(
    val jobDispatcher : JobDispatcher,
    val gameRepo : GameRepo,
    val userRepo: UserRepo,
    val tokenProvider: TokenProvider,
    val appConfig: AppConfig,
    val serverBackup : ServerBackup
) : ScreenModel {

    var games = mutableStateListOf<GameModel>()
    var showAdminTools = mutableStateOf(false)
    var showEditGames = mutableStateOf(false)
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
        launchIo(jobDispatcher, ::onException){
            userRepo.getUserSelf().let { user ->
                showAdminTools.value = listOf(
                    User.Role.Admin
                ).contains(user.role)

                showEditGames.value = listOf(
                    User.Role.Admin,
                    User.Role.Contributor
                ).contains(user.role)
            }
        }
    }

    fun backupServer() = launchIo(jobDispatcher, ::onException){
        serverBackup.backup()
    }

    private fun onException(t: Throwable){
        System.err.println(t.message)
        pageLoading.value = false
    }

}