package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
actual fun ImageEdit(
    modifier : Modifier,
    icon : ImageResource,
    error : String,
    onIconChange : (filePath : String) -> Unit
) {

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 10.dp)
                .border(
                    BorderStroke(2.dp, SolidColor(MaterialTheme.colors.primary)),
                    RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
        ) {
            ItemIcon(
                icon,
                size = 100.dp
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
}