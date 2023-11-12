package com.jaehl.gameTool.common.ui.screens.login.usecases

class ValidateRegisterPassword {
    operator fun invoke(password : String) : ValidationResult {
        return if(password.isEmpty()){
            ValidationResult(
                success = false,
                errorMessage = "you most enter a password"
            )
        }
        else {
            ValidationResult(
                success = true,
            )
        }
    }
}