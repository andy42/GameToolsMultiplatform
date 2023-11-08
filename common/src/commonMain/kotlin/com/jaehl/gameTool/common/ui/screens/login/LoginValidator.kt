package com.jaehl.gameTool.common.ui.screens.login



class LoginValidator {
    var listener : LoginValidatorListener? = null

    fun onValidate(userName : String, password : String) : Boolean{
        var valid = onValidateUserName(userName)
        valid = onValidatePassword(password) && valid
        return valid
    }

    fun onValidateUserName(email : String) : Boolean {
        if(email.isEmpty()){
            listener?.onLoginUserNameError("you most enter an user name")
            return false
        }
        return true
    }

    fun onValidatePassword(password : String) : Boolean {
        if(password.isEmpty()){
            listener?.onLoginPasswordError("you most enter a password")
            return false
        }
        return true
    }

    interface LoginValidatorListener {
        fun onLoginUserNameError(error : String)
        fun onLoginPasswordError(error : String)
    }
}