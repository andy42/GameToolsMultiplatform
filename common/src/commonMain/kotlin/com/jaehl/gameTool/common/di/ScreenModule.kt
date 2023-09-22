package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthProvider
import com.jaehl.gameTool.common.data.repo.*
import com.jaehl.gameTool.common.data.service.ImageService
import com.jaehl.gameTool.common.ui.screens.collectionDetails.CollectionDetailsScreenModel
import com.jaehl.gameTool.common.ui.screens.collectionList.CollectionListScreenModel
import com.jaehl.gameTool.common.ui.screens.gameDetails.GameDetailsScreenModel
import com.jaehl.gameTool.common.ui.screens.gameDetails.ItemImporter
import com.jaehl.gameTool.common.ui.screens.home.HomeScreenModel
import com.jaehl.gameTool.common.ui.screens.itemDetails.ItemDetailsScreenModel
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditScreenModel
import com.jaehl.gameTool.common.ui.screens.itemList.ItemListScreenModel
import com.jaehl.gameTool.common.ui.screens.login.LoginScreenModel
import com.jaehl.gameTool.common.ui.screens.login.LoginValidator
import com.jaehl.gameTool.common.ui.screens.login.RegisterValidator
import com.jaehl.gameTool.common.ui.screens.users.UsersScreenModel
import com.jaehl.gameTool.common.ui.screens.collectionEdit.CollectionEditScreenModel
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditValidator
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
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

        bind<GameDetailsScreenModel> { factory {config : GameDetailsScreenModel.Config ->
            GameDetailsScreenModel(
                instance<JobDispatcher>(),
                config = config,
                instance<GameRepo>(),
                instance<ItemImporter>(),
            )
        }}

        bind<ItemListScreenModel> { factory {config : ItemListScreenModel.Config ->
            ItemListScreenModel(
                instance<JobDispatcher>(),
                instance<AuthProvider>(),
                config = config,
                appConfig = instance<AppConfig>(),
                itemRepo = instance<ItemRepo>()
            )
        }}

        bind<ItemDetailsScreenModel> { provider {
            ItemDetailsScreenModel(
                instance<JobDispatcher>(),
                instance<AuthProvider>(),
                itemRepo = instance<ItemRepo>(),
                instance<RecipeRepo>(),
                instance<AppConfig>(),
                instance<ItemRecipeNodeUtil>()
            )}}


        bind<ItemEditScreenModel> { provider {
            ItemEditScreenModel(
                instance<JobDispatcher>(),
                itemRepo = instance<ItemRepo>(),
                imageService = instance<ImageService>(),
                instance<AppConfig>(),
                instance<AuthProvider>(),
                ItemEditValidator()
            )}}

        bind<CollectionDetailsScreenModel> { provider {
            CollectionDetailsScreenModel(
                instance<JobDispatcher>(),
                instance<CollectionRepo>(),
                instance<ItemRepo>(),
                instance<AppConfig>(),
                instance<AuthProvider>(),
            )}}

        bind<CollectionEditScreenModel> { provider {
            CollectionEditScreenModel(
                instance<JobDispatcher>(),
                instance<CollectionRepo>(),
                instance<ItemRepo>(),
                instance<AppConfig>(),
                instance<AuthProvider>(),
            )}}

        bind<CollectionListScreenModel> { provider {
            CollectionListScreenModel(
                instance<JobDispatcher>(),
                instance<CollectionRepo>()
            )}}
    }
}
