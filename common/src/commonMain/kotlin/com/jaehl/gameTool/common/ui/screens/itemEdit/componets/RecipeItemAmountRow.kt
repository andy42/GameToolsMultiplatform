package com.jaehl.gameTool.common.ui.screens.itemEdit.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.componets.ItemIcon
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel

@Composable
fun RecipeItemAmountRow(
    enabled: Boolean,
    recipeId : Int,
    isInput : Boolean,
    index : Int,
    itemAmount : ItemAmountViewModel,
    openItemPicker : (recipeId : Int, isInput : Boolean, itemId : Int?) -> Unit,
    onItemAmountChange : (recipeId : Int, isInput : Boolean, itemId : Int, amount : String) -> Unit,
    onItemAmountDelete : (recipeId : Int, isInput : Boolean, itemId : Int) -> Unit
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(if(index.mod(2) == 0) AppColor.rowBackgroundEven else AppColor.rowBackgroundOdd),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            enabled = enabled,
            modifier = Modifier.width(200.dp).padding(top = 10.dp),
            onClick = {
                openItemPicker(recipeId,isInput, itemAmount.itemModel.id)
            },
            border = BorderStroke(1.dp, MaterialTheme.colors.secondary),
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemIcon(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp),
                    itemAmount.itemModel.iconPath
                )
                Text(
                    itemAmount.itemModel.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
            }
        }

        OutlinedTextField(
            enabled = enabled,
            modifier =
                Modifier
                    .padding(start = 10.dp)
                    .width(80.dp)
            ,
            value = if(itemAmount.amount == 0) "" else  itemAmount.amount.toString(),
            onValueChange = { value ->
                onItemAmountChange(
                    recipeId,
                    isInput,
                    itemAmount.itemModel.id,
                    value.filter { it.isDigit()}
                )
            },
            label = { Text("count") }
        )
        IconButton(
            enabled = enabled,
            modifier = Modifier
                .requiredWidth(IntrinsicSize.Max)
                .padding(start = 10.dp)
            ,
            content = {
                Icon(
                    Icons.Outlined.Delete,
                    "delete",
                    tint = Color.Black)
            },
            onClick = {
                onItemAmountDelete(
                    recipeId,
                    isInput,
                    itemAmount.itemModel.id
                )
            }
        )
    }
}