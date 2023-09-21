package com.jaehl.gameTool.android

import com.jaehl.gameTool.common.App
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.jaehl.gameTool.apiClientRetrofit.di.ApiClientRetrofitModule
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.di.DataModule
import com.jaehl.gameTool.common.di.ScreenModule
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val di = DI {
                bind<AppConfig>() { provider { AppConfig(baseUrl = "http://192.168.1.112:8080") }}
                import(DataModule.create())
                import(ScreenModule.create())
                import(ApiClientRetrofitModule.create())
            }
            App(di)

        }
    }
}