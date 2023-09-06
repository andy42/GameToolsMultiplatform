package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.painter.Painter

import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

sealed class ImageResource() {
    data class ImageLocalResource(
        val url : String
    ) : ImageResource()
    data class ImageApiResource(
        val url : String,
        val authHeader : String
    ) : ImageResource()
}

@Composable
fun ItemIcon(imageResource : ImageResource, modifier : Modifier = Modifier, size : Dp = 40.dp){
//    val file = LocalFiles.getFile(iconPath ?: "")
//    if (file.exists() && !iconPath.isNullOrBlank()) {
//        val imageBitmap: ImageBitmap = remember(file) {
//            loadImageBitmap(file.inputStream())
//        }
//        Image(
//            painter = BitmapPainter(image = imageBitmap),
//            contentDescription = "",
//            modifier = modifier.width(size).height(size)
//        )
//    } else {
//        Box(modifier = modifier.width(size).height(size))
//    }

    when(imageResource) {
        is ImageResource.ImageLocalResource -> {
            Box(
                modifier = modifier
                    .width(size)
                    .height(size)
            )
        }
        is ImageResource.ImageApiResource -> {
            val painterResource: Resource<Painter> = asyncPainterResource(imageResource.url) {
                coroutineContext = Job() + Dispatchers.IO

                // Customizes HTTP request
                requestBuilder { // this: HttpRequestBuilder
                    header("Authorization", imageResource.authHeader)
                }
            }
            KamelImage(
                modifier = modifier
                    .width(size)
                    .height(size),
                resource = painterResource,
                contentDescription = "Profile",
            )
        }

    }
}