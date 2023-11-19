package com.jaehl.gameTool.common.ui.screens.login.usecases

data class ValidationResult(
    val success : Boolean,
    val errorMessage : String? = null
)
