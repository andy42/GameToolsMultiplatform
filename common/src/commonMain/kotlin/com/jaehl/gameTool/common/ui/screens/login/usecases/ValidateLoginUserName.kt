package com.jaehl.gameTool.common.ui.screens.login.usecases


class ValidateLoginUserName {
    operator fun invoke(userName : String) : ValidationResult {
        return if(userName.isEmpty()){
            ValidationResult(
                success = false,
                errorMessage = "you most enter an user name"
            )
        }
        else {
            ValidationResult(
                success = true,
            )
        }
    }
}