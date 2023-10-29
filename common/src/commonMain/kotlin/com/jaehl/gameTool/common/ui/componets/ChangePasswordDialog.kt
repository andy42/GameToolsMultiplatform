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
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.ui.AppColor

@Composable
fun ChangePasswordDialog(
    title : String,
    password : TextFieldValue,
    reEnterPassword : TextFieldValue,
    onPasswordChange : (value : String) -> Unit,
    onReEnterPasswordChange : (value : String) -> Unit,
    onChangePasswordClick : () -> Unit,
    onCloseClick : () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .clickable(interactionSource = MutableInteractionSource(), indication = null, onClick = {})
        .background(AppColor.dialogBackground)) {
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
                    Text(title)
                },
                navigationIcon = null
            )
            StyledPasswordOutlinedTextField(
                password,
                modifier = Modifier
                    .padding(top = 5.dp),
                label = { Text("Password") },
                onValueChange = { value ->
                    onPasswordChange(value)
                }
            )
            StyledPasswordOutlinedTextField(
                reEnterPassword,
                modifier = Modifier
                    .padding(top = 5.dp),
                label = { Text("Re-enter Password") },
                onValueChange = { value ->
                    onReEnterPasswordChange(value)
                }
            )
            Row (
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
            ) {
                Button(
                    modifier = Modifier
                        .padding(end = 20.dp),
                    onClick = {
                        onChangePasswordClick()
                    }
                ) {
                    Text("Change Password")
                }
                Button(
                    onClick = {
                        onCloseClick()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                ) {
                    Text("Close")
                }
            }
        }
    }
}