package com.jaehl.gameTool.apiClientKtor.di

import com.jaehl.gameTool.apiClientKtor.data.service.*
import com.jaehl.gameTool.apiClientKtor.data.util.ExceptionHandler
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.*
import io.ktor.client.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

object ApiClientKtorModule {
    fun create() = DI.Module(name = "ApiClientKtor") {
        bind<UserService> { provider {
            UserServiceKtor(
                instance<HttpClient>(),
                instance<AppConfig>(),
                ExceptionHandler()
            )
        }}

        bind<RequestUtil> { provider {
            RequestUtil(
                instance<HttpClient>(),
                instance<AppConfig>(),
                ExceptionHandler(),
                instance<TokenProvider>()
            )
        }}

        bind<GameService> { provider {
            GameServiceKtor(
                instance<RequestUtil>()
            )
        }}

        bind<ItemService> { provider {
            ItemServiceKtor(
                instance<RequestUtil>()
            )
        }}

        bind<RecipeService> { provider {
            RecipeServiceKtor(
                instance<RequestUtil>()
            )
        }}

        bind<CollectionService> {provider {
            CollectionServiceKtor(
                instance<RequestUtil>()
            )
        }}

        bind<BackupService> {provider {
            BackupServiceKtor(
                instance<RequestUtil>()
            )
        }}

        bind<ImageService> { provider {
            ImageServiceKtor(
                instance<HttpClient>(),
                instance<AppConfig>(),
                ExceptionHandler(),
                instance<TokenProvider>(),
            )
        }}
    }
}