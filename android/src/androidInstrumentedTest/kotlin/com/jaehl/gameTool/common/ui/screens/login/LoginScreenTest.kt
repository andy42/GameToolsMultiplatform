package com.jaehl.gameTool.common.ui.screens.login

import com.jaehl.gameTool.common.DeviceRobot
import org.junit.Test

import com.jaehl.gameTool.common.ui.screens.login.LoginScreenRobot.TextFieldId

class LoginScreenTest {

    private var deviceRobot = DeviceRobot()

    @Test
    fun loginTest(){
        val userName = "test"
        val password = "password"

        deviceRobot
            .setup()
            .logoutIfNeeded()
            .textFieldEnterValue(TextFieldId.userName, userName)
            .textFieldEnterValue(TextFieldId.password, password)
            .assertTextFieldValue(TextFieldId.userName, userName)
            .clickTextFieldShowPassword(TextFieldId.password)
            .assertTextFieldValue(TextFieldId.password, password)
            .loginClickAndTransition()
            .assertTitleHome()
    }

    @Test
    fun loginTextFieldErrorTest(){
        val userNameError = "you most enter an user name"
        val passwordError = "you most enter a password"

        deviceRobot
            .setup()
            .logoutIfNeeded()
            .textFieldEnterValue(TextFieldId.userName, "")
            .textFieldEnterValue(TextFieldId.password, "")
            .clickButton(LoginScreenRobot.ButtonId.loginButton)
            .assertTextFieldErrorText(TextFieldId.userName, userNameError)
            .assertTextFieldErrorText(TextFieldId.password, passwordError)
    }

    @Test
    fun loginErrorDialogTest(){
        deviceRobot
            .setup()
            .logoutIfNeeded()
            .textFieldEnterValue(TextFieldId.userName, "aa")
            .textFieldEnterValue(TextFieldId.password, "aa")
            .clickButton(LoginScreenRobot.ButtonId.loginButton)
            .waitUnitLoadingFinished()
            .assertErrorDialog("Login Error", "Login credentials incorrect")
    }
}