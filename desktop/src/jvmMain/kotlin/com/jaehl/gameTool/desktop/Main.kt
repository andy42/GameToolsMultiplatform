package com.jaehl.gameTool.desktop

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.jaehl.gameTool.apiClientKtor.di.ApiClientKtorModule
import com.jaehl.gameTool.apiClientRetrofit.data.DebugSslSocketFactory
import com.jaehl.gameTool.apiClientRetrofit.di.ApiClientRetrofitModule
import com.jaehl.gameTool.common.App
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthLocalStore
import com.jaehl.gameTool.common.di.DataModule
import com.jaehl.gameTool.common.di.ScreenModule
import com.jaehl.gameTool.desktop.data.AuthLocalStoreJsonFile
import com.jaehl.gameTool.desktop.data.LocalFileSettings
import com.jaehl.gameTool.desktop.data.LocalFiles
import com.jaehl.gameTool.desktop.data.LocalFilesImp
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.config.batikSvgDecoder
import io.kamel.image.config.resourcesFetcher
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.kodein.di.*


fun main() = application {
    val windowState = rememberWindowState(width = 720.dp, height = 800.dp)
    Window(title = "Game Tools", onCloseRequest = ::exitApplication, state = windowState) {
        val di = DI {
            bind<LocalFiles> {
                provider {
                    LocalFilesImp()
                }
            }
            bind<LocalFileSettings> {
                provider {
                    LocalFileSettings(
                        userHomeDirectory = "gameTools"
                    )
                }
            }
            bind<AppConfig> { provider { AppConfig(baseUrl = "https://gametoolsapi.63bit.com:5443") } }
            //bind<AppConfig> { provider { AppConfig(baseUrl = "http://0.0.0.0:8080") } }
            bind<AuthLocalStore> {
                singleton {
                    AuthLocalStoreJsonFile(
                        instance<LocalFiles>(),
                        instance<LocalFileSettings>()
                    )
                }
            }
            bind<HttpClient> {
                singleton {
                    val debugSslSocketFactory = DebugSslSocketFactory()

                    HttpClient(OkHttp) {
                        expectSuccess = true
                        install(ContentNegotiation) {
                            json()
                        }
                        engine {
                            config {
                                sslSocketFactory(debugSslSocketFactory.buildSSLSocketFactory(), debugSslSocketFactory.buildTrustManager())
                                hostnameVerifier(debugSslSocketFactory.buildHostnameVerifier())
                            }
                        }
                    }
                }
            }
            import(DataModule.create())
            import(ScreenModule.create())
            import(ApiClientRetrofitModule.create(trustAllCerts = true, addDelay = true))
            //import(ApiClientKtorModule.create())
        }

        val httpClient : HttpClient by di.instance<HttpClient>()

        val desktopConfig = KamelConfig {
            takeFrom(KamelConfig.Default)
            resourcesFetcher()
            batikSvgDecoder()
            httpFetcher(httpClient)
        }

        CompositionLocalProvider(LocalKamelConfig provides desktopConfig) {
            App(di)
        }
    }
}
