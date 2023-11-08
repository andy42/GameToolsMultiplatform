package com.jaehl.gameTool.common.ui.componets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
expect fun ImageEdit(
    modifier : Modifier,
    title : String,
    icon : ImageResource,
    error : String,
    onIconChange : (filePath : String) -> Unit,
    width : Dp,
    height : Dp
)
