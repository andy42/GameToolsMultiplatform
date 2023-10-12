package com.jaehl.gameTool.common.ui.screens.accountDetails

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.model.User
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.screens.launchIo

class AccountDetailsScreenModel(
    private val jobDispatcher: JobDispatcher,
    private val tokenProvider: TokenProvider,
    private val userRepo: UserRepo
) : ScreenModel {
    val viewModel = mutableStateOf(ViewModel())
    val logoutEvent = mutableStateOf(false)

    private fun onException(t : Throwable) {
        System.err.println(t.message)
    }

    fun setup() = launchIo(jobDispatcher, ::onException) {
        viewModel.value =  userRepo.getUserSelf().toViewModel()
    }

    fun logoutClick() = launchIo(jobDispatcher, ::onException) {
        tokenProvider.clearTokens()
        logoutEvent.value = true
    }

    fun User.toViewModel() : ViewModel{
        return ViewModel(
            userName = this.userName,
            role = this.role.name
        )
    }

    data class ViewModel(
        val userName : String = "",
        val role : String = ""
    )
}