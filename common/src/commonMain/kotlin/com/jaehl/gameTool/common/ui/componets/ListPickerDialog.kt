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
fun <T>ListPickerDialog(
    title: String,
    list : List<ListItem<T>>,
    onClose : () -> Unit,
    positiveText : String,
    selectedIndex : Int,
    onItemClick : (index : Int) -> Unit,
    onItemPicked : (data : T) -> Unit
){
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
            DialogTitleBar(
                title = title,
                onClose = onClose
            )

            list.forEachIndexed{ index, listItem ->
                ItemRow(
                    listItem,
                    index,
                    selectedIndex,
                    onItemClick
                )
            }

            Row (
                modifier = Modifier
                    .padding(bottom = 10.dp)
            ) {
                Button(
                    modifier = Modifier
                        .padding(end = 20.dp),
                    onClick = {
                        list.getOrNull(selectedIndex)?.data?.let {
                            onItemPicked(it)
                        }
                        onClose()
                    },
                    enabled = (selectedIndex != -1)
                ) {
                    Text(positiveText)
                }
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun <T>ItemRow(
    item : ListItem<T>,
    index : Int,
    selectedIndex : Int,
    onItemClick : (index : Int) -> Unit,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(index)
            }
            .background(
                if(selectedIndex == index) MaterialTheme.colors.secondary else MaterialTheme.colors.surface

            )
            .padding(10.dp)

    ) {
        Text(
            text = item.title,
            color = if(selectedIndex == index) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onSurface
        )
    }
}

data class ListItem<T>(
    val title : String,
    val data : T
)