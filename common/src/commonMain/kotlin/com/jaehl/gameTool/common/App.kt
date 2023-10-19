package com.jaehl.gameTool.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import cafe.adriel.voyager.navigator.Navigator
import com.jaehl.gameTool.common.ui.screens.login.LoginScreen
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.KamelConfigBuilder
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.ktor.client.*
import io.ktor.client.engine.*
import org.kodein.di.DI
import org.kodein.di.compose.withDI
import org.kodein.di.instance

val lightColors = lightColors(
    secondary = Color(0xffbfa456),
    secondaryVariant = Color(0xffbfa456),
    onSecondary = Color.Black
)

@Composable
fun App(di : DI) = withDI(di){
    MaterialTheme(
        colors = lightColors
    ) {
//        val httpClient : HttpClient by di.instance()
//        val desktopConfig = KamelConfigBuilder()
//            .apply {
//                httpFetcher(httpClient)
//            }
//            .build()
        Navigator(screen = LoginScreen())
    }
}