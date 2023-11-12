package com.jaehl.gameTool.common.ui.screens.login.usecases

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel

interface RegisterUseCase {
    suspend operator fun invoke(
        userName : String,
        email : String,
        password : String,
        showDialog : (dialogViewModel : DialogViewModel) -> Unit,
        showLoading : (loading : Boolean) -> Unit
    ) : Boolean
}

class RegisterUseCaseImp(
    private val jobDispatcher : JobDispatcher,
    private val userRepo : UserRepo,
    private val uiExceptionHandler : UiExceptionHandler
) : RegisterUseCase {

    override suspend fun invoke(
        userName: String,
        email: String,
        password: String,
        showDialog: (dialogViewModel: DialogViewModel) -> Unit,
        showLoading: (loading: Boolean) -> Unit
    ): Boolean {
        showLoading(true)

        try {
            userRepo.register(
                userName = userName,
                email = email,
                password = password
            )
            showLoading(false)
            return true
        }

        catch (t: Throwable) {
            showDialog(uiExceptionHandler.handelException(t))
            showLoading(false)
            return false
        }
    }
}