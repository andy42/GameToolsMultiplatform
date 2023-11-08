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
            .textFieldEnterValue(TextFieldId.UserName, userName)
            .textFieldEnterValue(TextFieldId.Password, password)
            .assertTextFieldValue(TextFieldId.UserName, userName)
            .clickTextFieldShowPassword(TextFieldId.Password)
            .assertTextFieldValue(TextFieldId.Password, password)
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
            .textFieldEnterValue(TextFieldId.UserName, "")
            .textFieldEnterValue(TextFieldId.Password, "")
            .clickButton(LoginScreenRobot.ButtonId.LoginButton)
            .assertTextFieldErrorText(TextFieldId.UserName, userNameError)
            .assertTextFieldErrorText(TextFieldId.Password, passwordError)
    }

    @Test
    fun loginErrorDialogTest(){
        deviceRobot
            .setup()
            .logoutIfNeeded()
            .textFieldEnterValue(TextFieldId.UserName, "aa")
            .textFieldEnterValue(TextFieldId.Password, "aa")
            .clickButton(LoginScreenRobot.ButtonId.LoginButton)
            .waitUnitLoadingFinished()
            .assertErrorDialog("Login Error", "Login credentials incorrect")
    }
}