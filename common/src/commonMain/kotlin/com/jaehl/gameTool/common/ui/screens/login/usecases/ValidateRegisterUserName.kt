package com.jaehl.gameTool.common.ui.screens.login.usecases

import com.jaehl.gameTool.common.ui.Strings

class ValidateRegisterUserName {
    operator fun invoke(userName : String) : ValidationResult {
        return if(userName.isEmpty()){
            ValidationResult(
                success = false,
                errorMessage = Strings.Login.validateRegisterUserNameEmpty
            )
        }
        else {
            ValidationResult(
                success = true,
            )
        }
    }
}