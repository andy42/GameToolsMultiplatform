package com.jaehl.gameTool.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
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
import org.kodein.di.*


fun main() = application {
    val windowState = rememberWindowState(width = 720.dp, height = 800.dp)
    Window(onCloseRequest = ::exitApplication, state = windowState) {
        val di = DI {
            bind<LocalFiles> {
                provider {
                    LocalFilesImp()
                }
            }
            bind <LocalFileSettings>{ provider {
                LocalFileSettings(
                    userHomeDirectory = "gameTools"
                )
            } }
            bind<AppConfig> { provider { AppConfig(baseUrl = "http://0.0.0.0:8080") }}
            bind<AuthLocalStore> { singleton {
                AuthLocalStoreJsonFile(
                    instance<LocalFiles>(),
                    instance<LocalFileSettings>()
                )
            }}
            import(DataModule.create())
            import(ScreenModule.create())
            import(ApiClientRetrofitModule.create())
        }
        App(di)
    }
}
