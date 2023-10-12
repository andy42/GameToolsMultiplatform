package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.*
import com.jaehl.gameTool.common.data.repo.*
import com.jaehl.gameTool.common.data.service.*
import com.jaehl.gameTool.common.ui.util.ItemImporter
import com.jaehl.gameTool.common.ui.util.ItemRecipeInverter
import com.jaehl.gameTool.common.ui.util.ItemRecipeNodeUtil
import com.jaehl.gameTool.common.ui.util.ServerBackup
import org.kodein.di.*

object DataModule {
    fun create() = DI.Module(name = "commonData") {
        bind<JobDispatcher> { singleton { JobDispatcher() }}

        bind<UserRepo> { singleton {  UserRepoImp(
            instance<UserService>(),
            instance<AuthLocalStore>()
        ) }}

        bind<TokenProvider> { provider {
            instance<UserRepo>() as TokenProvider
        }}

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

        bind<CollectionRepo> {
            singleton {
                CollectionRepoImp(
                    instance<JobDispatcher>(),
                    instance<CollectionService>()
                )
            }
        }

        bind<BackupRepo> { singleton { BackupRepoImp(
            instance<BackupService>()
        ) }}

        bind<ItemImporter> { singleton { ItemImporter(
            instance<ItemService>(),
            instance<ImageService>(),
            instance<RecipeService>()
        ) }}

        bind<ItemRecipeNodeUtil>{ singleton { ItemRecipeNodeUtil(
            instance<ItemRepo>(),
            instance<RecipeRepo>(),
            instance<AppConfig>(),
            instance<TokenProvider>()
        ) }}

        bind<ItemRecipeInverter> { singleton {
            ItemRecipeInverter()
        }}

        bind<ServerBackup> {
            singleton {
                ServerBackup(
                    instance(),
                    instance(),
                    instance(),
                    instance(),
                    instance(),
                    instance(),
                    instance()
                )
            }
        }
    }
}