package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppBar(
    title: String,
    showBackButton: Boolean = false,
    enabledBackButton : Boolean = true,
    onBackClick : () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    var navigationIcon : @Composable (() -> Unit)? = null
    if(showBackButton) navigationIcon = {
        IconButton(
            enabled = enabledBackButton,
            content = {
                Icon(Icons.Outlined.ArrowBack, "back", tint = Color.White)
            },
            onClick = {
                onBackClick()
            }
        )
    }

    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = navigationIcon,
        actions = actions
    )
}