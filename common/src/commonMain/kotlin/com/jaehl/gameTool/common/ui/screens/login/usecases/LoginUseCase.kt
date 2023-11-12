package com.jaehl.gameTool.common.ui.screens.login.usecases

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.ErrorDialogViewModel

interface LoginUseCase {
    suspend operator fun invoke(
        userName : String,
        password : String,
        showDialog : (dialogViewModel : DialogViewModel) -> Unit,
        showLoading : (loading : Boolean) -> Unit
    ) : Boolean
}

class LoginUseCaseImp(
    private val jobDispatcher : JobDispatcher,
    private val userRepo : UserRepo,
    private val uiExceptionHandler : UiExceptionHandler
) : LoginUseCase {
    override suspend fun invoke(
        userName: String,
        password: String,
        showDialog : (dialogViewModel : DialogViewModel) -> Unit,
        showLoading : (loading : Boolean) -> Unit
    ) : Boolean {

        showLoading(true)

        try {
            userRepo.login(userName, password)
            showLoading(false)
            return true
        }

        catch (t: Throwable) {
            when (t) {
                is UiException.ForbiddenError -> {
                    showDialog(
                        ErrorDialogViewModel(
                            title = "Login Error",
                            message = "Login credentials incorrect"
                        )
                    )
                }
                else -> {
                    showDialog(uiExceptionHandler.handelException(t))
                }
            }
            showLoading(false)
            return false
        }
    }
}