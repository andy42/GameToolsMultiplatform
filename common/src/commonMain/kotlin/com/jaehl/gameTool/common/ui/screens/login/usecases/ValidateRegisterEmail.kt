package com.jaehl.gameTool.common.ui.screens.login.usecases

class ValidateRegisterEmail {
    operator fun invoke(email : String) : ValidationResult {
        return if(email.isEmpty()){
            ValidationResult(
                success = false,
                errorMessage = "you most enter an email"
            )
        }
        else {
            ValidationResult(
                success = true,
            )
        }
    }
}