package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun StyledOutlinedTextField(
    textFieldValue: TextFieldValue,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation : VisualTransformation = VisualTransformation.None,
    label: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        label = label,
        value = textFieldValue.value,
        isError = textFieldValue.hasError(),
        enabled = enabled,
        readOnly = readOnly,
        onValueChange = onValueChange,
        visualTransformation = visualTransformation
    )
    if(textFieldValue.hasError()){
        Text(
            text = textFieldValue.error,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}