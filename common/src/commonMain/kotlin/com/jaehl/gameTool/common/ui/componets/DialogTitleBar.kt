package com.jaehl.gameTool.common.ui.componets

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DialogTitleBar(title: String, onClose : () -> Unit){
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = null,
        actions = {
            IconButton(content = {
                Icon(Icons.Outlined.Close, "Close", tint = Color.White)
            }, onClick = {
                onClose()
            })
        }
    )
}