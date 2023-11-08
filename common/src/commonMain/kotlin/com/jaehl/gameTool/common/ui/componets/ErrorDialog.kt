package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.TestTags

@Composable
fun ErrorDialog(
    title : String,
    message : String,
    buttonText : String,
    onClick : () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .clickable(interactionSource = MutableInteractionSource(), indication = null, onClick = {})
        .background(AppColor.dialogBackground)
        .testTag(TestTags.General.error_dialog)
    ) {
        Column(
            modifier = Modifier
                .width(400.dp)
                .padding(top = 20.dp, bottom = 20.dp)
                .align(Alignment.Center)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.testTag(TestTags.General.error_dialog_title),
                        text = title
                    )
                },
                backgroundColor = MaterialTheme.colors.error,
                contentColor = MaterialTheme.colors.onError,
                navigationIcon = null
            )
            Text(
                modifier = Modifier
                    .padding(10.dp)
                    .testTag(TestTags.General.error_dialog_message),
                text = message
            )
            Row (
                modifier = Modifier
                    .padding(bottom = 10.dp)
            ) {
                Button(
                    modifier = Modifier
                        .padding(end = 20.dp),
                    onClick = {
                        onClick()
                    }
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}