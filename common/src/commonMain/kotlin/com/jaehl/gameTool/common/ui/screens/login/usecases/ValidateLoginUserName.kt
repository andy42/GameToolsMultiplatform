package com.jaehl.gameTool.common.ui.screens.login.usecases

import com.jaehl.gameTool.common.ui.Strings


class ValidateLoginUserName {
    operator fun invoke(userName : String) : ValidationResult {
        return if(userName.isEmpty()){
            ValidationResult(
                success = false,
                errorMessage = Strings.Login.validateLoginUserNameEmpty
            )
        }
        else {
            ValidationResult(
                success = true,
            )
        }
    }
}