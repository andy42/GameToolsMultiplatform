package com.jaehl.gameTool.common.ui.viewModel

interface DialogViewModel {}

object ClosedDialogViewModel : DialogViewModel

data class ItemCategoryPickerDialogViewModel(
    val searchText : String = ""
) : DialogViewModel

data class ErrorDialogViewModel(
    val title : String,
    val message : String
) : DialogViewModel

data class ForceLogoutDialogViewModel(
    val title : String = "Auth Error",
    val message : String = "A Auth error is forcing you to logout"
) : DialogViewModel