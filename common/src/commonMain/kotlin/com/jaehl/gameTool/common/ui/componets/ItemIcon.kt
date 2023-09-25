package com.jaehl.gameTool.common.ui.componets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class ImageResource {
    data class ImageLocalResource(
        val url : String
    ) : ImageResource()
    data class ImageApiResource(
        val url : String,
        val authHeader : String
    ) : ImageResource()
}

@Composable
expect fun ItemIcon(imageResource : ImageResource, modifier : Modifier = Modifier, size : Dp = 40.dp)