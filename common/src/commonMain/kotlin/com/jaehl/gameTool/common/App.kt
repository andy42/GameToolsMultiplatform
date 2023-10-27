package com.jaehl.gameTool.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import cafe.adriel.voyager.navigator.Navigator
import com.jaehl.gameTool.common.ui.screens.login.LoginScreen
import org.kodein.di.DI
import org.kodein.di.compose.withDI

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
        Navigator(screen = LoginScreen())
    }
}