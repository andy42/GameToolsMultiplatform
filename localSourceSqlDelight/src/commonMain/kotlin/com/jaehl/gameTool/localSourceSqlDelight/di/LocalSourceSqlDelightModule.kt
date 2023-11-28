package com.jaehl.gameTool.localSourceSqlDelight.di

import app.cash.sqldelight.adapter.primitive.FloatColumnAdapter
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
                    ),
                    RecipeCraftedAtEntityAdapter = RecipeCraftedAtEntity.Adapter(
                        recipe_idAdapter = IntColumnAdapter,
                        item_idAdapter = IntColumnAdapter,
                        game_idAdapter = IntColumnAdapter
                    ),
                    RecipeEntityAdapter = RecipeEntity.Adapter(
                        idAdapter = IntColumnAdapter,
                        game_idAdapter = IntColumnAdapter
                    ),
                    RecipeInputEntityAdapter = RecipeInputEntity.Adapter(
                        recipe_idAdapter = IntColumnAdapter,
                        item_idAdapter = IntColumnAdapter,
                        game_idAdapter = IntColumnAdapter,
                        amountAdapter = IntColumnAdapter
                    ),
                    RecipeOutputEntityAdapter = RecipeOutputEntity.Adapter(
                        recipe_idAdapter = IntColumnAdapter,
                        item_idAdapter = IntColumnAdapter,
                        game_idAdapter = IntColumnAdapter,
                        amountAdapter = IntColumnAdapter
                    ),
                    UserEntityAdapter = UserEntity.Adapter(
                        idAdapter = IntColumnAdapter
                    ),
                    UserSelfEntityAdapter = UserSelfEntity.Adapter(
                        user_idAdapter = IntColumnAdapter
                    ),
                    CollectionEntityAdapter = CollectionEntity.Adapter(
                        idAdapter = IntColumnAdapter,
                        user_idAdapter = IntColumnAdapter,
                        game_idAdapter = IntColumnAdapter
                    ),
                    CollectionGroupEntityAdapter = CollectionGroupEntity.Adapter(
                        idAdapter = IntColumnAdapter,
                        collection_idAdapter = IntColumnAdapter,
                        cost_ReductionAdapter = FloatColumnAdapter
                    ),
                    CollectionItemAmountEntityAdapter = CollectionItemAmountEntity.Adapter(
                        group_idAdapter = IntColumnAdapter,
                        item_idAdapter = IntColumnAdapter,
                        amountAdapter = IntColumnAdapter
                    ),
                    ItemRecipePreferenceEntityAdapter = ItemRecipePreferenceEntity.Adapter(
                        group_idAdapter = IntColumnAdapter,
                        item_idAdapter = IntColumnAdapter,
                        recipe_id_preferenceAdapter = IntColumnAdapter
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
                UserLocalSourceSqlDelight(
                    instance<Database>()
                )
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
                CollectionLocalSourceSqlDelight(
                    instance<Database>()
                )
            }
        }

        bind<RecipeLocalSource> {
            singleton {
                RecipeLocalSourceSqlDelight(
                    instance<Database>()
                )
            }
        }
    }
}