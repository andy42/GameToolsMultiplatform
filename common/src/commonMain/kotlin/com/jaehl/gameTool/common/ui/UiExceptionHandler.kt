package com.jaehl.gameTool.common.ui

import com.jaehl.gameTool.common.ui.util.UiException
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.ErrorDialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.ForceLogoutDialogViewModel

interface UiExceptionHandler {
    fun handelException(e : Throwable) : DialogViewModel
}

class UiExceptionHandlerImp() : UiExceptionHandler {
    private fun handelUiException(e: UiException): DialogViewModel {
        return when (e) {
            is UiException.ForbiddenError -> {
                ForceLogoutDialogViewModel()
            }
            is UiException.ServerConnectionError -> {
                ErrorDialogViewModel(
                    title = "Connection Error",
                    message = "Oops, seems like you can not connect to the server"
                )
            }
            is UiException.GeneralError -> {
                ErrorDialogViewModel(
                    title = "Error",
                    message = e.message ?: "Oops something went wrong"
                )
            }
            else -> {
                ErrorDialogViewModel(
                    title = "Error",
                    message = "Oops something went wrong"
                )
            }
        }
    }

    override fun handelException(e: Throwable): DialogViewModel {
        return if(e is UiException) {
            handelUiException(e)
        } else  {
            ErrorDialogViewModel(
                title = "Error",
                message = "Oops something went wrong"
            )
        }
    }
}