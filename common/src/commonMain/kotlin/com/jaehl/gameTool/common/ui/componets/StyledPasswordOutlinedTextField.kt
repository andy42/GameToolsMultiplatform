package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun StyledPasswordOutlinedTextField(
    textFieldValue: TextFieldValue,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    label: @Composable (() -> Unit)? = null,
    visibility: MutableState<Boolean> = remember { mutableStateOf(false) },
    testTag : String = "",
) {
    OutlinedTextField (
        modifier = modifier
            .testTag(testTag),
        label = label,
        value = textFieldValue.value,
        isError = textFieldValue.hasError(),
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        onValueChange = onValueChange,
        trailingIcon = {
            if(visibility.value)
                IconButton(content = {
                    Icon(IconsCustom.rememberVisibility(), "Show Password", tint = MaterialTheme.colors.onSurface)
                }, onClick = {
                    visibility.value = false
                })
            else {
                IconButton(
                    modifier = Modifier
                        .testTag("${testTag}_hidePassword"),
                    content = {
                        Icon(IconsCustom.rememberVisibilityOff(), "Hide Password", tint = MaterialTheme.colors.onSurface)
                    },
                    onClick = {
                        visibility.value = true
                    }
                )
            }
        },
        visualTransformation = if(visibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
    if(textFieldValue.hasError()){
        Text(
            text = textFieldValue.error,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(start = 16.dp)
                .testTag("${testTag}_error")
        )
    }
}