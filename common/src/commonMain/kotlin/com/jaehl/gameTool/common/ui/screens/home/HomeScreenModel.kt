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
import com.jaehl.gameTool.common.ui.util.UiException

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
    var userUnverified = mutableStateOf(false)
    var showEditGames = mutableStateOf(false)
    var pageLoading = mutableStateOf(false)

    val dialogConfig = mutableStateOf<DialogConfig>(DialogConfig.Closed)

    fun setup() = launchIo(jobDispatcher, ::onException){
        dataRefresh()
    }

    suspend fun dataRefresh() {
        launchIo(jobDispatcher, ::onException){
            val user = userRepo.getUserSelf()

            val userUnverified = listOf(
                User.Role.Unverified
            ).contains(user.role)

            this.userUnverified.value = userUnverified

            showAdminTools.value = listOf(
                User.Role.Admin
            ).contains(user.role)

            showEditGames.value = listOf(
                User.Role.Admin,
                User.Role.Contributor
            ).contains(user.role)

            if(userUnverified) return@launchIo

            val games = gameRepo.getGames().map {
                it.toGameModel(appConfig, tokenProvider)
            }
            this.games.postSwap(games)
            this.pageLoading.value = false
        }
    }

    fun backupServer() = launchIo(jobDispatcher, ::onException){
        serverBackup.backup()
    }

    fun onRefreshClick() = launchIo(jobDispatcher, ::onException){
        dataRefresh()
    }

    fun closeDialog() {
        dialogConfig.value = DialogConfig.Closed
    }

    private fun handelUiException(e : UiException) {
        when (e) {
            is UiException.ForbiddenError -> {
                dialogConfig.value = DialogConfig.ErrorDialog(
                    title = "Login Error",
                    message = "Login credentials incorrect"
                )
            }
            is UiException.ServerConnectionError -> {
                dialogConfig.value = DialogConfig.ErrorDialog(
                    title = "Connection Error",
                    message = "Oops, seems like you can not connect to the server"
                )
            }
            else -> {
                dialogConfig.value = DialogConfig.ErrorDialog(
                    title = "Error",
                    message = "Oops something went wrong"
                )
            }
        }
    }

    private fun onException(t: Throwable){
        if (t is UiException){
            handelUiException(t)
        }
        pageLoading.value = false
    }

    sealed class DialogConfig {
        data object Closed : DialogConfig()
        data class ErrorDialog(
            val title : String,
            val message : String
        ) : DialogConfig()
    }
}