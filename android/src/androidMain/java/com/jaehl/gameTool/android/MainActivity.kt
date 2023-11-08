package com.jaehl.gameTool.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Scaffold
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.jaehl.gameTool.android.data.AuthPreferencesDataStore
import com.jaehl.gameTool.apiClientKtor.di.ApiClientKtorModule
import com.jaehl.gameTool.apiClientRetrofit.data.DebugSslSocketFactory
import com.jaehl.gameTool.common.App
import com.jaehl.gameTool.common.data.AppConfig
import com.jaehl.gameTool.common.data.AuthLocalStore
import com.jaehl.gameTool.common.di.DataModule
import com.jaehl.gameTool.common.di.ScreenModule
import com.jaehl.gameTool.common.ui.screens.login.LoginScreen
import io.ktor.client.*
import io.ktor.client.engine.android.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider
import org.kodein.di.singleton

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

class MainActivity : AppCompatActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val di = DI {
                bind<AppConfig> { provider { AppConfig(baseUrl = "https://gametoolsapi.63bit.com:5443") } }
                bind<AuthLocalStore> {
                    singleton {
                        AuthPreferencesDataStore(
                            context = this@MainActivity
                        )
                    }
                }
                bind<HttpClient> {
                    singleton {
                        HttpClient(Android) {
                            expectSuccess = true
                            install(ContentNegotiation) {
                                json()
                            }
                        }
                    }
                }
                import(DataModule.create())
                import(ScreenModule.create())
                //import(ApiClientRetrofitModule.create())
                import(ApiClientKtorModule.create())
            }

            Scaffold(
                // Enables for all composables in the hierarchy.
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                }
            ) {
                App(di)
            }
        }
    }
}