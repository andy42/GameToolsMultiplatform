package com.jaehl.gameTool.common.ui.screens.login

class RegisterValidator {

    var listener : RegisterValidatorListener? = null

    fun onValidate(userName : String, email : String, password : String, reEnterPassword : String) : Boolean {

        var valid = onValidateUserName(userName)
        valid = onValidateEmail(email) && valid
        valid = onValidatePassword(password) && valid
        valid = onValidateReEnterPassword(password, reEnterPassword) && valid
        return valid
    }

    fun onValidateUserName(userName : String) : Boolean {
        if(userName.isEmpty()){
            listener?.onRegisterUserNameError("you most enter a userName")
            return false
        }
        return true
    }

    fun onValidateEmail(email : String) : Boolean {
        if(email.isEmpty()){
            listener?.onRegisterEmailError("you most enter an email")
            return false
        }
        return true
    }

    fun onValidatePassword(password : String) : Boolean {
        if(password.isEmpty()){
            listener?.onRegisterPasswordError("you most enter a password")
            return false
        }
        return true
    }

    fun onValidateReEnterPassword(password : String, reEnterPassword : String) : Boolean {
        if(reEnterPassword.isEmpty()){
            listener?.onRegisterReEnterPasswordError("you most re-enter your password")
            return false
        }
        if(password != reEnterPassword){
            listener?.onRegisterReEnterPasswordError("you password does not match")
            return false
        }
        return true
    }

    interface RegisterValidatorListener {
        fun onRegisterUserNameError(error: String)
        fun onRegisterEmailError(error : String)
        fun onRegisterPasswordError(error : String)
        fun onRegisterReEnterPasswordError(error : String)
    }
}