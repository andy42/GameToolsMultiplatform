package com.jaehl.gameTool.common

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

import cafe.adriel.voyager.navigator.Navigator
import com.jaehl.gameTool.common.ui.screens.login.LoginScreen
import org.kodein.di.DI
import org.kodein.di.compose.withDI

@Composable
fun App(di : DI) = withDI(di){
    MaterialTheme {
        Navigator(screen = LoginScreen())
    }
}
