package com.jaehl.gameTool.common.di

import com.jaehl.gameTool.common.data.local.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

object LocalSourceInMemoryModule {

    fun create() = DI.Module(name = "LocalSourceInMemory") {
        bind<GameLocalSource> {
            singleton {
                GameLocalSourceInMemory()
            }
        }

        bind<UserLocalSource> {
            singleton {
                UserLocalSourceInMemory()
            }
        }

        bind<ItemLocalSource> {
            singleton {
                ItemLocalSourceInMemory()
            }
        }

        bind<CollectionLocalSource> {
            singleton {
                CollectionLocalSourceInMemory()
            }
        }

        bind<RecipeLocalSource> {
            singleton {
                RecipeLocalSourceInMemory()
            }
        }
    }
}