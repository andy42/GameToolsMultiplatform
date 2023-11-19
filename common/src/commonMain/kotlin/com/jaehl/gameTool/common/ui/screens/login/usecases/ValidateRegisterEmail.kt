package com.jaehl.gameTool.common.ui.screens.login.usecases

import com.jaehl.gameTool.common.ui.Strings

class ValidateRegisterEmail {
    operator fun invoke(email : String) : ValidationResult {
        return if(email.isEmpty()){
            ValidationResult(
                success = false,
                errorMessage = Strings.Login.validateRegisterEmailEmpty
            )
        }
        else {
            ValidationResult(
                success = true,
            )
        }
    }
}