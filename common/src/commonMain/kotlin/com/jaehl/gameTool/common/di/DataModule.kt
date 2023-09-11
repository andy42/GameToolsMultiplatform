package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.*
import com.jaehl.gameTool.common.data.repo.*
import com.jaehl.gameTool.common.data.service.*
import com.jaehl.gameTool.common.ui.screens.gameDetails.ItemImporter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import org.kodein.di.*

object DataModule {
    fun create() = DI.Module(name = "commonData") {
        bind<JobDispatcher> { singleton { JobDispatcher() }}

        bind<UserRepo> { singleton {  UserRepoImp(
            instance<UserService>(),
            instance<AuthProvider>()
        ) }}

        bind<GameRepo> { singleton {  GameRepoImp(
            instance<GameService>()
        ) }}

        bind<ItemRepo> { singleton { ItemRepoImp(
            instance<JobDispatcher>(),
            instance<ItemService>()
        ) }}

        bind<RecipeRepo> { singleton { RecipeRepoImp(
            instance<JobDispatcher>(),
            instance<RecipeService>()
        ) }}

        bind<AuthProvider> { singleton {
            AuthProviderImp()
        }}

        bind<ItemImporter> { singleton { ItemImporter(
            instance<ItemService>(),
            instance<ImageService>(),
            instance<RecipeService>()
        ) }}

        bind<ItemRecipeNodeUtil>{ singleton { ItemRecipeNodeUtil(
            instance<ItemRepo>(),
            instance<RecipeRepo>(),
            instance<AppConfig>(),
            instance<AuthProvider>()
        ) }}
    }
}