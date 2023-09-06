package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.repo.GameRepo
import com.jaehl.gameTool.common.data.repo.ItemRepo
import com.jaehl.gameTool.common.data.repo.UserRepo
import com.jaehl.gameTool.common.data.service.ImageService
import com.jaehl.gameTool.common.data.service.ItemService
import com.jaehl.gameTool.common.ui.screens.gameDetails.GameDetailsScreenModel
import com.jaehl.gameTool.common.ui.screens.home.HomeScreenModel
import com.jaehl.gameTool.common.ui.screens.itemDetails.ItemDetailsScreenModel
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditScreenModel
import com.jaehl.gameTool.common.ui.screens.itemList.ItemListScreenModel
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

        bind<HomeScreenModel> { provider {
            HomeScreenModel(
                instance<JobDispatcher>(),
                instance<GameRepo>()
            )}}

        bind<GameDetailsScreenModel>() { factory {config : GameDetailsScreenModel.Config ->
            GameDetailsScreenModel(
                instance<JobDispatcher>(),
                config = config,
                instance<GameRepo>(),
                instance<ItemService>(),
                instance<ImageService>()
            )
        }}

        bind<ItemListScreenModel>() { factory {config : ItemListScreenModel.Config ->
            ItemListScreenModel(
                instance<JobDispatcher>(),
                instance<AuthProvider>(),
                config = config,
                itemRepo = instance<ItemRepo>()
            )
        }}

        bind<ItemDetailsScreenModel>() { factory {config : ItemDetailsScreenModel.Config ->
            ItemDetailsScreenModel(
                instance<JobDispatcher>(),
                instance<AuthProvider>(),
                config = config,
                itemRepo = instance<ItemRepo>()
            )
        }}

        bind<ItemEditScreenModel>() { factory {config : ItemEditScreenModel.Config ->
            ItemEditScreenModel(
                instance<JobDispatcher>(),
                config = config,
                itemRepo = instance<ItemRepo>()
            )
        }}
    }
}
