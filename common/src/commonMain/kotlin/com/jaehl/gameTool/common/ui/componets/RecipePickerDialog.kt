package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel

@Composable
fun RecipePickerDialog(
    title: String,
    recipePickerData : RecipePickerData,
    onClose : () -> Unit,
    onRecipeClick : (recipeId : Int?) -> Unit,
    onRecipeConfirmClick : () -> Unit
) {
    val state : ScrollState = rememberScrollState()
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
                .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DialogTitleBar(
                title = title,
                onClose = onClose
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state)
            ) {

                RecipePickerRowNone(
                    modifier = Modifier
                        .padding(10.dp),
                    selectedRecipeId = recipePickerData.selectedRecipeId,
                    onRecipeClick = onRecipeClick
                )

                recipePickerData.recipes.forEachIndexed { index, recipeViewModel ->
                    RecipePickerRow(
                        modifier = Modifier
                            .padding(10.dp),
                        selectedRecipeId = recipePickerData.selectedRecipeId,
                        data = recipeViewModel,
                        onRecipeClick = onRecipeClick
                    )
                }

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier,
                        onClick = {
                            onRecipeConfirmClick()
                            onClose()
                        },
                    ) {
                        Text("Change Recipe")
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
}

@Composable
fun RecipePickerRowNone(
    modifier : Modifier,
    selectedRecipeId : Int?,
    onRecipeClick : (recipeId : Int?) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onRecipeClick(null)
            }
    ) {
        Column(
            Modifier
                .background(
                    if (selectedRecipeId == null) MaterialTheme.colors.secondary else MaterialTheme.colors.surface
                )
                .padding(10.dp)
        ) {
            Text(
                text = "None",
                color = if (selectedRecipeId == null) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
fun RecipePickerRow(
    modifier : Modifier,
    selectedRecipeId : Int?,
    data : RecipePickerData.RecipeViewModel,
    onRecipeClick : (recipeId : Int?) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onRecipeClick(data.id)
            }
    ) {
        Column(
            Modifier
                .background(
                    if (selectedRecipeId == data.id) MaterialTheme.colors.secondary else MaterialTheme.colors.surface
                )
                .padding(10.dp)
        ) {
            data.output.forEach {
                ItemAmount(it, (selectedRecipeId == data.id))
            }
            Box(
                Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colors.primary)
            )
            data.input.forEach {
                ItemAmount(it, (selectedRecipeId == data.id))
            }
        }
    }
}

@Composable
fun ItemAmount(
    itemAmount: ItemAmountViewModel,
    selected : Boolean
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            itemAmount.amount.toString(),
            modifier = Modifier
                .width(50.dp)
                .padding(start = 10.dp),
            textAlign = TextAlign.Center

        )
        ItemIcon(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            itemAmount.itemModel.iconPath,
        )
        Text(
            itemAmount.itemModel.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            color = if (selected) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onSurface
        )
    }
}

data class RecipePickerData(
    val selectedRecipeId : Int?,
    val recipes : List<RecipeViewModel>
) {
    data class RecipeViewModel(
        val id : Int,
        val input : List<ItemAmountViewModel> = listOf(),
        val output : List<ItemAmountViewModel> = listOf()
    )
}