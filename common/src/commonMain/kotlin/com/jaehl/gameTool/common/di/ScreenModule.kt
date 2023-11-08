package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.repo.*
import com.jaehl.gameTool.common.data.service.ImageService
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.UiExceptionHandlerImp
import com.jaehl.gameTool.common.ui.screens.userDetails.UserDetailsScreenModel
import com.jaehl.gameTool.common.ui.screens.backupList.BackupListScreenModel
import com.jaehl.gameTool.common.ui.screens.collectionDetails.CollectionDetailsScreenModel
import com.jaehl.gameTool.common.ui.screens.collectionList.CollectionListScreenModel
import com.jaehl.gameTool.common.ui.screens.gameDetails.GameDetailsScreenModel
import com.jaehl.gameTool.common.ui.util.ItemImporter
import com.jaehl.gameTool.common.ui.screens.home.HomeScreenModel
import com.jaehl.gameTool.common.ui.screens.itemDetails.ItemDetailsScreenModel
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditScreenModel
import com.jaehl.gameTool.common.ui.screens.itemList.ItemListScreenModel
import com.jaehl.gameTool.common.ui.screens.login.LoginScreenModel
import com.jaehl.gameTool.common.ui.screens.login.LoginValidator
import com.jaehl.gameTool.common.ui.screens.login.RegisterValidator
import com.jaehl.gameTool.common.ui.screens.users.UsersScreenModel
import com.jaehl.gameTool.common.ui.screens.collectionEdit.CollectionEditScreenModel
import com.jaehl.gameTool.common.ui.screens.gameEdit.GameEditScreenModel
import com.jaehl.gameTool.common.ui.screens.gameEdit.GameEditValidator
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditValidator
import com.jaehl.gameTool.common.ui.util.ItemRecipeInverter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.util.ServerBackup
import org.kodein.di.*

object ScreenModule {
    fun create() = DI.Module(name = "Screens") {

        bind<UiExceptionHandler>() {
            provider {
                UiExceptionHandlerImp()
            }
        }

        bind<LoginScreenModel> { provider {
            LoginScreenModel(
                instance<JobDispatcher>(),
                instance<UserRepo>(),
                instance<TokenProvider>(),
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
                instance<GameRepo>(),
                instance<UserRepo>(),
                instance<ItemRepo>(),
                instance<RecipeRepo>(),
                instance<CollectionRepo>(),
                instance<TokenProvider>(),
                instance<AppConfig>(),
                instance<ServerBackup>(),
                instance<UiExceptionHandler>()
            )}}

        bind<GameDetailsScreenModel> { provider {
            GameDetailsScreenModel(
                instance<JobDispatcher>(),
                instance<GameRepo>(),
                instance<ItemImporter>(),
                instance<TokenProvider>(),
                instance<AppConfig>(),
            )}}

        bind<GameEditScreenModel> { provider {
            GameEditScreenModel(
                instance<JobDispatcher>(),
                instance<ItemRepo>(),
                instance<GameRepo>(),
                instance<ImageService>(),
                instance<AppConfig>(),
                instance<TokenProvider>(),
                GameEditValidator()
            )}}

        bind<ItemListScreenModel> { provider {
            ItemListScreenModel(
                instance<JobDispatcher>(),
                instance<TokenProvider>(),
                appConfig = instance<AppConfig>(),
                itemRepo = instance<ItemRepo>(),
                userRepo = instance<UserRepo>(),
                gameRepo = instance<GameRepo>(),
                instance<UiExceptionHandler>()
            )}}

        bind<ItemDetailsScreenModel> { provider {
            ItemDetailsScreenModel(
                instance<JobDispatcher>(),
                instance<TokenProvider>(),
                itemRepo = instance<ItemRepo>(),
                userRepo = instance<UserRepo>(),
                instance<RecipeRepo>(),
                instance<AppConfig>(),
                instance<ItemRecipeNodeUtil>(),
                instance<ItemRecipeInverter>()
            )}}


        bind<ItemEditScreenModel> { provider {
            ItemEditScreenModel(
                instance<JobDispatcher>(),
                itemRepo = instance<ItemRepo>(),
                recipeRepo = instance<RecipeRepo>(),
                gameRepo = instance<GameRepo>(),
                imageService = instance<ImageService>(),
                instance<AppConfig>(),
                instance<TokenProvider>(),
                ItemEditValidator()
            )}}

        bind<CollectionDetailsScreenModel> { provider {
            CollectionDetailsScreenModel(
                instance<JobDispatcher>(),
                instance<CollectionRepo>(),
                instance<ItemRepo>(),
                instance<RecipeRepo>(),
                instance<AppConfig>(),
                instance<TokenProvider>(),
                instance<ItemRecipeNodeUtil>(),
                instance<ItemRecipeInverter>(),
                instance<UiExceptionHandler>()
            )}}

        bind<CollectionEditScreenModel> { provider {
            CollectionEditScreenModel(
                instance<JobDispatcher>(),
                instance<CollectionRepo>(),
                instance<ItemRepo>(),
                instance<GameRepo>(),
                instance<AppConfig>(),
                instance<TokenProvider>(),
                instance<UiExceptionHandler>()
            )}}

        bind<CollectionListScreenModel> { provider {
            CollectionListScreenModel(
                instance<JobDispatcher>(),
                instance<CollectionRepo>()
            )}}

        bind<BackupListScreenModel> { provider {
            BackupListScreenModel(
                instance<JobDispatcher>(),
                instance<BackupRepo>()
            )}}

        bind<UserDetailsScreenModel> {
            provider {
                UserDetailsScreenModel(
                    instance<JobDispatcher>(),
                    instance<TokenProvider>(),
                    instance<UserRepo>()
                )
            }
        }
    }
}
