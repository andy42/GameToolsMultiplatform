package com.jaehl.gameTool.common.ui.screens.login

import org.junit.Test
import kotlin.test.assertEquals

class LoginValidatorTest {

    private val loginValidatorListener = object :LoginValidator.LoginValidatorListener {
        var loginEmailError : String? = null
        override fun onLoginUserNameError(error: String) {
            loginEmailError = error
        }

        var loginPasswordError : String? = null
        override fun onLoginPasswordError(error: String) {
            loginPasswordError = error
        }

        fun clear(){
            loginEmailError = null
            loginPasswordError = null
        }
    }

    @Test
    fun`loginEmailError empty`(){
        val loginValidator = LoginValidator()
        loginValidator.listener = loginValidatorListener
        loginValidatorListener.clear()

        loginValidator.onValidateUserName("")
        assertEquals("you most enter an email", loginValidatorListener.loginEmailError)
    }

    @Test
    fun`loginPasswordError empty`(){
        val loginValidator = LoginValidator()
        loginValidator.listener = loginValidatorListener
        loginValidatorListener.clear()

        loginValidator.onValidatePassword("")
        assertEquals("you most enter a password", loginValidatorListener.loginPasswordError)
    }
}