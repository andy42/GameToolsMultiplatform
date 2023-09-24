package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.ui.AppColor
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
            SwitchWithTitle(
                modifier = Modifier
                    .padding(top = 10.dp),
                title = "Show Base Ingredients",
                checked = recipeSettings.showBaseIngredients,
                onCheckedChange = {
                    onRecipeSettingsChange(
                        recipeSettings.copy(
                            showBaseIngredients = it
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
            text = title
        )
    }
}