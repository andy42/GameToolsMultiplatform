package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.JobDispatcher
import com.jaehl.gameTool.common.data.*
import com.jaehl.gameTool.common.data.local.*
import com.jaehl.gameTool.common.data.repo.*
import com.jaehl.gameTool.common.data.service.*
import com.jaehl.gameTool.common.ui.UiExceptionHandler
import com.jaehl.gameTool.common.ui.UiExceptionHandlerImp
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
            instance<AuthLocalStore>(),
            instance<UserLocalSource>()
        ) }}

        bind<TokenProvider> { provider {
            instance<UserRepo>() as TokenProvider
        }}

        bind<GameLocalSource> {
            singleton {
                GameLocalSourceInMemory()
            }
        }

        bind<UserLocalSource>() {
            singleton {
                UserLocalSourceInMemory()
            }
        }

        bind<ItemLocalSource>() {
            singleton {
                ItemLocalSourceInMemory()
            }
        }

        bind<CollectionLocalSource>(){
            singleton {
                CollectionLocalSourceInMemory()
            }
        }

        bind<RecipeLocalSource>() {
            singleton {
                RecipeLocalSourceInMemory()
            }
        }

        bind<GameRepo> { singleton {  GameRepoImp(
            instance<GameService>(),
            instance<GameLocalSource>(),
        ) }}

        bind<ItemRepo> { singleton { ItemRepoImp(
            instance<JobDispatcher>(),
            instance<ItemService>(),
            instance<ItemLocalSource>()
        ) }}

        bind<RecipeRepo> { singleton { RecipeRepoImp(
            instance<JobDispatcher>(),
            instance<RecipeService>(),
            instance<RecipeLocalSource>()
        ) }}

        bind<CollectionRepo> {
            singleton {
                CollectionRepoImp(
                    instance<JobDispatcher>(),
                    instance<CollectionService>(),
                    instance<CollectionLocalSource>()
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
            //instance<RecipeRepo>(),
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