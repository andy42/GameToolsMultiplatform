package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.kamel.core.utils.URI

sealed class DragState {
    data class Dragging(val imageResource: ImageResource) : DragState()
    data object NonDragging : DragState()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun ImageEdit(
    modifier : Modifier,
    icon : ImageResource,
    error : String,
    onIconChange : (filePath : String) -> Unit
) {
    val dragState = remember { mutableStateOf<DragState>(DragState.NonDragging) }

    Box(
        modifier = modifier
    ) {

        var borderModifier = Modifier
            .align(Alignment.Center)
            .padding(top = 10.dp)
        if(dragState.value is DragState.Dragging) {
            borderModifier = borderModifier
                .dashedBorder(
                    width = 1.dp,
                    color = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(10.dp),
                    on = 4.dp,
                    off = 4.dp
                )
        } else {
            borderModifier = borderModifier
                .border(
                    BorderStroke(
                        width = 1.dp,
                        brush = SolidColor(
                            if(error.isEmpty()) MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                            else MaterialTheme.colors.error
                        )
                    )
                    ,RoundedCornerShape(10.dp)
                )
        }

        borderModifier = borderModifier.onExternalDrag(
            onDragStart = {
                if(it.dragData is DragData.FilesList){
                    val files = it.dragData as DragData.FilesList
                    files.readFiles().firstOrNull()?.let { filePath ->
                        dragState.value = DragState.Dragging(
                            ImageResource.ImageLocalResource(
                                URI(filePath).path
                            )
                        )
                    }
                }
            },
            onDragExit = {
                dragState.value = DragState.NonDragging
            },
            onDrop = {
                dragState.value = DragState.NonDragging

                if(it.dragData is DragData.FilesList){
                    val files = it.dragData as DragData.FilesList
                    files.readFiles().firstOrNull()?.let { filePath ->
                        onIconChange(URI(filePath).path)
                    }
                }
            }
        )
        .padding(10.dp)


        Box(
            modifier = borderModifier
        ) {
            ItemIcon(
                if(dragState.value is DragState.Dragging) {
                    (dragState.value as DragState.Dragging).imageResource
                } else {
                    icon
                },
                size = 150.dp
            )
        }
        Text(
            "Icon",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp)
                .background(MaterialTheme.colors.surface)
                .padding(start = 5.dp, end = 5.dp)
        )
    }
    if(error.isNotEmpty()){
        Text(
            text = error,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}