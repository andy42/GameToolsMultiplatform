package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.UserRepo
import com.jaehl.gameTool.common.data.UserRepoImp
import com.jaehl.gameTool.common.data.service.UserService
import org.kodein.di.*

object DataModule {
    fun create() = DI.Module(name = "commonData") {
        bind<JobDispatcher> { singleton { JobDispatcher() }}
        bind<UserRepo> { singleton {  UserRepoImp(instance<UserService>()) }}
    }
}