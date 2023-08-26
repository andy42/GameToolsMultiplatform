package com.jaehl.gameTool.common.ui.screens.login



class LoginValidator {
    var listener : LoginValidatorListener? = null

    fun onValidate(email : String, password : String) : Boolean{
        var valid = onValidateEmail(email)
        valid = onValidatePassword(password) && valid
        return valid
    }

    fun onValidateEmail(email : String) : Boolean {
        if(email.isEmpty()){
            listener?.onLoginEmailError("you most enter an email")
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
        fun onLoginEmailError(error : String)
        fun onLoginPasswordError(error : String)
    }
}