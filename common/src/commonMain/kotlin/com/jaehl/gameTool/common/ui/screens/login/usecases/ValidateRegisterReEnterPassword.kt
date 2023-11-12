package com.jaehl.gameTool.common.ui.screens.login.usecases

class ValidateRegisterReEnterPassword {
    operator fun invoke(password : String, reEnterPassword : String) : ValidationResult {
        return if(password != reEnterPassword){
            ValidationResult(
                success = false,
                errorMessage = "your password does not match"
            )
        }
        else {
            ValidationResult(
                success = true,
            )
        }
    }
}