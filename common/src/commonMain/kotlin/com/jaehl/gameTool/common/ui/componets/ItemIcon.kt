package com.jaehl.gameTool.common.ui.componets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class ImageResource {
    data class ImageLocalResource(
        val url : String
    ) : ImageResource() {
        fun getFileExtension() : String {
            return this.url.split(".").lastOrNull() ?: ""
        }
    }
    data class ImageApiResource(
        val url : String,
        val authHeader : String
    ) : ImageResource()
}

@Composable
expect fun ItemIcon(modifier : Modifier = Modifier, imageResource : ImageResource, contentScale : ContentScale = ContentScale.Fit)