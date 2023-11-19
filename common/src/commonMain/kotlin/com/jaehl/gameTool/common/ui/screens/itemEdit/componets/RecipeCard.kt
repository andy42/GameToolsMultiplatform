package com.jaehl.gameTool.common.ui.screens.itemEdit.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditScreenModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeCard(
    enabled : Boolean,
    recipeIndex : Int,
    recipe : ItemEditScreenModel.RecipeViewModel,
    openItemPicker : (recipeId : Int, isInput : Boolean, itemId : Int?) -> Unit,
    onItemAmountChange : (recipeId : Int, isInput : Boolean, itemId : Int, amount : String) -> Unit,
    onItemAmountDelete : (recipeId : Int, isInput : Boolean, itemId : Int) -> Unit,
    onAddRecipeCraftedAtClick : (recipeId : Int) -> Unit,
    onRecipeCraftedAtDelete : (recipeId : Int, itemId : Int) -> Unit,
    onRecipeDelete : (recipeId : Int) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 10.dp, top = 20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.surface)
            .padding(10.dp)
    ) {
        Text(text = "Recipe ${recipe.id + 1}")

        Text(text = "Crafted At", modifier = Modifier.padding(top = 20.dp))
        FlowRow(modifier = Modifier) {
            recipe.craftingAtList.forEach { item ->
                CraftedAtChip(enabled, recipe.id, item, onRecipeCraftedAtDelete)
            }
            Button(
                enabled =enabled,
                onClick = {
                    onAddRecipeCraftedAtClick(recipe.id)
                    //viewModel.onOpenItemPicker(ItemEditViewModel.ItemPickerType.CreatedAt(recipeIndex))
                },
                modifier = Modifier
                //.align(Alignment.CenterHorizontally)
            ) {
                Text("Add")
            }
        }

        Text(text = "Output", modifier = Modifier.padding(top = 20.dp))
        recipe.output.forEachIndexed { index, itemAmountViewModel ->
            RecipeItemAmountRow(
                enabled,
                recipe.id,
                isInput = false,
                index = index,
                itemAmount = itemAmountViewModel,
                openItemPicker = openItemPicker,
                onItemAmountChange = onItemAmountChange,
                onItemAmountDelete = onItemAmountDelete
            )
        }
        Button(
            enabled = enabled,
            modifier = Modifier.padding(top = 10.dp),
            onClick = {
                openItemPicker(
                    recipe.id,
                    false,
                    null
                )
            }
        ) {
            Text("Add Output")
        }
        Text(text = "Input", modifier = Modifier.padding(top = 20.dp))
        recipe.input.forEachIndexed { index, itemAmountViewModel ->
            RecipeItemAmountRow(
                enabled,
                recipeId = recipe.id,
                isInput = true,
                index = index,
                itemAmount = itemAmountViewModel,
                openItemPicker = openItemPicker,
                onItemAmountChange = onItemAmountChange,
                onItemAmountDelete = onItemAmountDelete
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                enabled = enabled,
                onClick = {
                    openItemPicker(
                        recipe.id,
                    true,
                    null
                    )
                }
            ) {
                Text("Add Input")
            }
            Button(
                enabled = enabled,
                onClick = {
                    onRecipeDelete(recipe.id)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
            ) {
                Text(
                    text ="Delete Recipe",
                    color = MaterialTheme.colors.onError
                )
            }
        }
    }
}