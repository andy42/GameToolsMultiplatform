package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.UserRepo
import com.jaehl.gameTool.common.ui.screens.login.LoginScreenModel
import com.jaehl.gameTool.common.ui.screens.login.LoginValidator
import com.jaehl.gameTool.common.ui.screens.login.RegisterValidator
import com.jaehl.gameTool.common.ui.screens.users.UsersScreenModel
import org.kodein.di.*

object ScreenModule {
    fun create() = DI.Module(name = "Screens") {

        bind<LoginScreenModel> { provider {
            LoginScreenModel(
                instance<JobDispatcher>(),
                instance<UserRepo>(),
                LoginValidator(),
                RegisterValidator()
            )}}

        bind<UsersScreenModel> { provider {
            UsersScreenModel(
                instance<JobDispatcher>(),
                instance<UserRepo>()
            )}}
    }
}
