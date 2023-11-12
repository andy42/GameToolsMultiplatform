package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.jaehl.gameTool.common.ui.TestTags

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
            modifier = Modifier
                .testTag(TestTags.General.nav_back_button),
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
            Text(
                modifier = Modifier.testTag(TestTags.General.nav_title),
                text = title
            )
        },
        navigationIcon = navigationIcon,
        actions = actions
    )
}