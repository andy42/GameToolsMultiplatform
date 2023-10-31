package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.lazyPainterResource
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.net.URI

@Composable
actual fun ItemIcon(modifier : Modifier, imageResource : ImageResource, contentScale : ContentScale){
    when(imageResource) {
        is ImageResource.ImageLocalResource -> {
            Box(
                modifier = modifier
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
                modifier = modifier,
                contentScale = contentScale,
                resource = painterResource,
                contentDescription = "Profile",
            )
        }

    }
}