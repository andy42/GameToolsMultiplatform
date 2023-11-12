package com.jaehl.gameTool.common.ui.screens.login


import com.jaehl.gameTool.common.DeviceRobot
import com.jaehl.gameTool.common.LoginCredentials
import org.junit.Test

import com.jaehl.gameTool.common.ui.screens.login.LoginScreenRobot.TextFieldId

class LoginScreenTest {

    private var deviceRobot = DeviceRobot()

    val loginCredentials = LoginCredentials(
        userName = "test",
        password = "password"
    )

    @Test
    fun loginTest(){

        deviceRobot
            .setup()
            .logoutIfNeeded()
            .textFieldEnterValue(TextFieldId.UserName, loginCredentials.userName)
            .textFieldEnterValue(TextFieldId.Password, loginCredentials.password)
            .assertTextFieldValue(TextFieldId.UserName, loginCredentials.userName)
            .clickTextFieldShowPassword(TextFieldId.Password)
            .assertTextFieldValue(TextFieldId.Password, loginCredentials.password)
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

    @Test
    fun testRun(){
        deviceRobot
            .setup()
            .logoutIfNeeded()
            .loginUser(loginCredentials.userName, loginCredentials.password)
            .clickGameRowAndTransition(0)
            .waitUnitLoadingFinished()
            .clickItemsButtonAndTransition()
            .clickItemRowAndTransition(0)
    }
}