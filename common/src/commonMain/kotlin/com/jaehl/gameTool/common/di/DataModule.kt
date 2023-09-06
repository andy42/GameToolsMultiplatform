package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.*
import com.jaehl.gameTool.common.data.repo.*
import com.jaehl.gameTool.common.data.service.GameService
import com.jaehl.gameTool.common.data.service.ItemService
import com.jaehl.gameTool.common.data.service.UserService
import org.kodein.di.*

object DataModule {
    fun create() = DI.Module(name = "commonData") {
        bind<JobDispatcher> { singleton { JobDispatcher() }}
        bind<UserRepo> { singleton {  UserRepoImp(instance<UserService>(), instance<AuthProvider>()) }}
        bind<GameRepo> { singleton {  GameRepoImp(instance<GameService>()) }}
        bind<ItemRepo> { singleton { ItemRepoImp(instance<JobDispatcher>(), instance<ItemService>()) }}
        bind<AuthProvider> { singleton { AuthProviderImp() }}
    }
}