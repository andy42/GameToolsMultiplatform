package com.jaehl.gameTool.common.ui.screens.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.Resource
import com.jaehl.gameTool.common.data.model.Game
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.extensions.postSwap
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.screens.launchIo
import com.jaehl.gameTool.common.ui.util.ServerBackup
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.ClosedDialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

class HomeScreenModel(
    private val jobDispatcher : JobDispatcher,
    private val gameRepo : GameRepo,
    private val userRepo: UserRepo,
    private val tokenProvider: TokenProvider,
    private val appConfig: AppConfig,
    private val serverBackup : ServerBackup,
    private val uiExceptionHandler : UiExceptionHandler
) : ScreenModel {

    var games = mutableStateListOf<GameModel>()
    var showAdminTools = mutableStateOf(false)
    var userUnverified = mutableStateOf(false)
    var showEditGames = mutableStateOf(false)
    var pageLoading = mutableStateOf(false)

    val logoutEvent = mutableStateOf(false)

    val dialogViewModel = mutableStateOf<DialogViewModel>(ClosedDialogViewModel)

    fun setup() = launchIo(jobDispatcher, ::onException){
        dataRefresh()
    }

    private suspend fun updateUi( userResource : Resource<User>, gamesResource : Resource<List<Game>>) {
        pageLoading.value = userResource is Resource.Loading || gamesResource is Resource.Loading

        if(userResource is Resource.Error){
            onException(userResource.exception)
            return
        }
        if(gamesResource is Resource.Error){
            onException(gamesResource.exception)
            return
        }

        val user = userResource.getDataOrThrow()
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

        this.games.postSwap(
            gamesResource.getDataOrThrow().map {
                it.toGameModel(appConfig, tokenProvider)
            }
        )
    }

    suspend fun dataRefresh() {
        pageLoading.value = true
        launchIo(jobDispatcher, ::onException){
            combine(
                userRepo.getUserSelFlow(),
                gameRepo.getGames()
            ) { user, games ->
                updateUi(user, games)
            }.collect()
        }
    }

    fun backupServer() = launchIo(jobDispatcher, ::onException){
        serverBackup.backup()
    }

    fun onRefreshClick() = launchIo(jobDispatcher, ::onException){
        dataRefresh()
    }

    fun closeDialog() {
        dialogViewModel.value = ClosedDialogViewModel
    }

    private fun onException(t: Throwable){
        if (t is UiException){
            dialogViewModel.value = uiExceptionHandler.handelException(t)
        }
        pageLoading.value = false
    }

    fun forceLogout() = launchIo(jobDispatcher, ::onException) {
        tokenProvider.clearTokens()
        logoutEvent.value = true
        closeDialog()
    }

    sealed class DialogConfig {
        data object Closed : DialogConfig()
        data class ErrorDialog(
            val title : String,
            val message : String
        ) : DialogConfig()
    }
}