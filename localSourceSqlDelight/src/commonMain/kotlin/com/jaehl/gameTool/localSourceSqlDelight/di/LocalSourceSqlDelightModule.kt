package com.jaehl.gameTool.localSourceSqlDelight.di

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.jaehl.gameTool.localSourceSqlDelight.Database
import com.jaehl.gameTool.common.data.local.*
import com.jaehl.gameTool.localSourceSqlDelight.data.local.*
import org.kodein.di.*

expect fun bindSqlDriver(diBuilder : DI.Builder)

object LocalSourceSqlDelightModule {
    fun create() = DI.Module(name = "LocalSourceSqlDelight") {

        bindSqlDriver(this)

        bind<Database> {
            provider {
                Database(
                    driver = instance<SqlDriver>(),
                    GameEntityAdapter = GameEntity.Adapter(
                        idAdapter = IntColumnAdapter,
                        iconAdapter = IntColumnAdapter,
                        bannerAdapter = IntColumnAdapter
                    ),
                    GameCategoryEntityAdapter = GameCategoryEntity.Adapter(
                        game_idAdapter = IntColumnAdapter,
                        category_idAdapter = IntColumnAdapter
                    ),
                    CategoryEntityAdapter = CategoryEntity.Adapter(
                        idAdapter = IntColumnAdapter
                    ),
                    ItemCategoryEntityAdapter = ItemCategoryEntity.Adapter(
                        item_idAdapter = IntColumnAdapter,
                        category_idAdapter = IntColumnAdapter
                    ),
                    ItemEntityAdapter = ItemEntity.Adapter(
                        idAdapter = IntColumnAdapter,
                        imageAdapter = IntColumnAdapter,
                        game_idAdapter = IntColumnAdapter
                    )
                )
            }
        }

        bind<GameLocalSource> {
            singleton {
                GameLocalSourceSqlDelight(
                    instance<Database>()
                )
            }
        }

        bind<UserLocalSource> {
            singleton {
                UserLocalSourceInMemory()
            }
        }

        bind<ItemLocalSource> {
            singleton {
                ItemLocalSourceSqlDelight(
                    instance<Database>()
                )
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