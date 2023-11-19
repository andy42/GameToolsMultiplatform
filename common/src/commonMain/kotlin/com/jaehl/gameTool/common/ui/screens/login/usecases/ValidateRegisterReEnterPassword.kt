package com.jaehl.gameTool.common.ui.screens.login.usecases

import com.jaehl.gameTool.common.ui.Strings

class ValidateRegisterReEnterPassword {
    operator fun invoke(password : String, reEnterPassword : String) : ValidationResult {
        return if(password != reEnterPassword){
            ValidationResult(
                success = false,
                errorMessage = Strings.Login.validateRegisterReEnterPasswordMismatch
            )
        }
        else {
            ValidationResult(
                success = true,
            )
        }
    }
}