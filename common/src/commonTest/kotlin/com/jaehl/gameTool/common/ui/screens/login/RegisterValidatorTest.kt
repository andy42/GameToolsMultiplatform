package com.jaehl.gameTool.common.ui.screens.login

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegisterValidatorTest {

    private val registerValidatorListener  = object : RegisterValidator.RegisterValidatorListener {
        var userNameError : String? = null
        override fun onRegisterUserNameError(error: String) {
            userNameError = error
        }

        var emailError : String? = null
        override fun onRegisterEmailError(error: String) {
            emailError = error
        }

        var passwordError : String? = null
        override fun onRegisterPasswordError(error: String) {
            passwordError = error
        }

        var reEnterPasswordError : String? = null
        override fun onRegisterReEnterPasswordError(error: String) {
            reEnterPasswordError = error
        }

        fun clear(){
            userNameError = null
            emailError = null
            passwordError = null
            reEnterPasswordError = null
        }
    }

    private fun buildRegisterValidator() : RegisterValidator{
        val registerValidator = RegisterValidator()
        registerValidator.listener = registerValidatorListener
        registerValidatorListener.clear()
        return registerValidator
    }

    @Test
    fun `valid`(){
        val registerValidator = buildRegisterValidator()
        assertTrue(
            registerValidator.onValidate(
                userName = "userName",
                email = "test@test.com",
                password = "password",
                reEnterPassword = "password"
            )
        )
        assertEquals(null, registerValidatorListener.userNameError)
        assertEquals(null, registerValidatorListener.emailError)
        assertEquals(null, registerValidatorListener.passwordError)
        assertEquals(null, registerValidatorListener.reEnterPasswordError)
    }

    @Test
    fun `userNameError empty`(){
        val registerValidator = buildRegisterValidator()
        assertFalse(
            registerValidator.onValidateUserName("")
        )
        assertEquals("you most enter a userName", registerValidatorListener.userNameError)
    }

    @Test
    fun `emailError empty`(){
        val registerValidator = buildRegisterValidator()
        assertFalse(
            registerValidator.onValidateEmail("")
        )
        assertEquals("you most enter an email", registerValidatorListener.emailError)
    }

    @Test
    fun `passwordError empty`(){
        val registerValidator = buildRegisterValidator()
        assertFalse(
            registerValidator.onValidatePassword("")
        )
        assertEquals("you most enter a password", registerValidatorListener.passwordError)
    }

    @Test
    fun `reEnterPasswordError empty`(){
        val registerValidator = buildRegisterValidator()
        assertFalse(
            registerValidator.onValidateReEnterPassword("", "")
        )
        assertEquals("you most re-enter your password", registerValidatorListener.reEnterPasswordError)
    }

    @Test
    fun `reEnterPasswordError mismatch`(){
        val registerValidator = buildRegisterValidator()
        assertFalse(
            registerValidator.onValidateReEnterPassword("", "password")
        )
        assertEquals("you password does not match", registerValidatorListener.reEnterPasswordError)
    }
}