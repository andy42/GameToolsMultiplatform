package com.jaehl.gameTool.common.ui.componets

import androidx.compose.runtime.Composable
import com.jaehl.gameTool.common.ui.viewModel.DialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.ErrorDialogViewModel
import com.jaehl.gameTool.common.ui.viewModel.ForceLogoutDialogViewModel

@Composable
fun ErrorDialog(
    dialogViewModel : DialogViewModel,
    onClose : () -> Unit
) {
    if(dialogViewModel is ErrorDialogViewModel){
        ErrorDialog(
            title = dialogViewModel.title,
            message = dialogViewModel.message,
            buttonText = "Ok",
            onClick = {
                onClose()
            }
        )
    }
}

@Composable
fun ForceLogoutDialog(
    dialogViewModel : DialogViewModel,
    onClose : () -> Unit
) {
    if(dialogViewModel is ForceLogoutDialogViewModel){
        ErrorDialog(
            title = dialogViewModel.title,
            message = dialogViewModel.message,
            buttonText = "Ok",
            onClick = {
                onClose()
            }
        )
    }
}