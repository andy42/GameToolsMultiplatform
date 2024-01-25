package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.viewModel.RecipeDisplayType
import com.jaehl.gameTool.common.ui.viewModel.RecipeSettings

@Composable
fun RecipeSettingsDialog(
    title: String,
    recipeSettings : RecipeSettings,
    onClose : () -> Unit,
    onRecipeSettingsChange : (settings : RecipeSettings) -> Unit
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

            SwitchWithTitle(
                modifier = Modifier
                    .padding(top = 20.dp),
                title = "Show Sub Ingredients",
                checked = !recipeSettings.collapseIngredients,
                onCheckedChange = {
                    onRecipeSettingsChange(
                        recipeSettings.copy(
                            collapseIngredients = !it
                        )
                    )
                }
            )
            ListPickerWithTitle(
                modifier = Modifier
                    .padding(top = 10.dp),
                title = "Display Type ",
                value = recipeSettings.displayType,
                list = RecipeDisplayType.values().map {
                    Pair<RecipeDisplayType, String>(it, it.displayName)
                },
                onValueChange = {
                    onRecipeSettingsChange(
                        recipeSettings.copy(
                            displayType = it
                        )
                    )
                }
            )

            Button(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 20.dp),
                onClick = {
                    onClose()
                }
            ){
                Text("Ok")
            }

        }
    }
}

@Composable
fun <T>ListPickerWithTitle(
    modifier: Modifier = Modifier,
    title : String,
    value : T,
    list : List<Pair<T, String>>,
    onValueChange : (value : T) -> Unit
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 10.dp),
            text = title,
            fontWeight = FontWeight.SemiBold
        )
        Column (
            modifier = modifier
                //.padding(top = 1.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(10.dp))
                .border(
                    BorderStroke(2.dp, SolidColor(MaterialTheme.colors.primary)),
                    RoundedCornerShape(10.dp)
                )
        ) {
            list.forEach {
                Text(
                    modifier = Modifier
                        .clickable {
                            onValueChange(it.first)
                        }
                        .background(if (value == it.first) MaterialTheme.colors.secondary else MaterialTheme.colors.surface)
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                        .padding(start = 10.dp,),
                    color = if (value == it.first) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onSurface,
                    text = it.second
                )
            }
        }
    }
}

@Composable
fun SwitchWithTitle(
    modifier: Modifier = Modifier,
    title : String,
    checked : Boolean,
    onCheckedChange : (value : Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checked,
            onCheckedChange = {
                onCheckedChange(it)
            }
        )
        Text(
            modifier = Modifier
                .padding(start = 10.dp),
            text = title,
            fontWeight = FontWeight.SemiBold
        )
    }
}