package com.jaehl.gameTool.common.ui.screens.login.usecases

import com.jaehl.gameTool.common.ui.Strings

class ValidateRegisterPassword {
    operator fun invoke(password : String) : ValidationResult {
        return if(password.isEmpty()){
            ValidationResult(
                success = false,
                errorMessage = Strings.Login.validateRegisterPasswordEmpty
            )
        }
        else {
            ValidationResult(
                success = true,
            )
        }
    }
}