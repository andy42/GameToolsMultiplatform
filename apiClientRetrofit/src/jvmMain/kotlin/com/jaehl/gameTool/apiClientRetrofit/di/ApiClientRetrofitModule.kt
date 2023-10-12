package com.jaehl.gameTool.apiClientRetrofit.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jaehl.gameTool.apiClientRetrofit.data.api.ServerApi
import com.jaehl.gameTool.apiClientRetrofit.data.service.*
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.repo.TokenProvider
import com.jaehl.gameTool.common.data.service.*
import okhttp3.OkHttpClient
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClientRetrofitModule {
    fun create() = DI.Module(name = "ApiClientRetrofit") {

        bind<OkHttpClient> { provider {
                OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .callTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build()
        }}

        bind<Gson> { provider {
            GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ")
                .create()
        }}

        bind<Retrofit> { provider {
            Retrofit.Builder()
                .baseUrl(instance<AppConfig>().baseUrl)
                .addConverterFactory(GsonConverterFactory.create(instance<Gson>()))
                //.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(instance<OkHttpClient>())
                .build()
        }}

        bind<ServerApi> { provider {
            instance<Retrofit>().create(ServerApi::class.java)
        }}

        bind<UserService> { provider {
            UserServiceRetrofit(
                instance<ServerApi>()
            )
        }}

        bind<GameService> { provider {
            GameServiceRetroFit(
                instance<ServerApi>(),
                instance<TokenProvider>()
            )
        }}

        bind<ItemService> { provider {
            ItemServiceRetroFit(
                instance<ServerApi>(),
                instance<TokenProvider>()
            )
        }}

        bind<ImageService> { provider {
            ImageServiceRetroFit(
                instance<ServerApi>(),
                instance<TokenProvider>()
            )
        }}

        bind<RecipeService> { provider {
            RecipeServiceRetroFit(
                instance<ServerApi>(),
                instance<TokenProvider>()
            )
        }}

        bind<CollectionService> { provider {
            CollectionServiceRetroFit(
                instance<ServerApi>(),
                instance<TokenProvider>()
            )
        }}

        bind<BackupService> { provider {
            BackupServiceRetroFit(
                instance<ServerApi>(),
                instance<TokenProvider>()
            )
        }}

    }
}