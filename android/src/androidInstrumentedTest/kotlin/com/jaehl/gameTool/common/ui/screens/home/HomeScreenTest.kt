package com.jaehl.gameTool.common.ui.screens.home

import com.jaehl.gameTool.common.DeviceRobot
import com.jaehl.gameTool.common.LoginCredentials
import com.jaehl.gameTool.common.ui.screens.login.LoginScreenRobot
import org.junit.Test

class HomeScreenTest {

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
            .loginUser(loginCredentials.userName, loginCredentials.password)
            .clickGameRowWithTitleAndTransition("Icarus")
            .clickItemsButtonAndTransition()
    }
}