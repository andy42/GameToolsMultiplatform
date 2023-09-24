package com.jaehl.gameTool.common.ui.componets

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

@Composable
expect fun ImageEdit(
    modifier : Modifier,
    icon : ImageResource,
    error : String,
    onIconChange : (filePath : String) -> Unit
)
