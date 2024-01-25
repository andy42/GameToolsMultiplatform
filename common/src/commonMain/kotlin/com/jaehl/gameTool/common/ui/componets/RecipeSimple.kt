package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.screens.itemDetails.CraftedAtChip
import com.jaehl.gameTool.common.ui.screens.itemDetails.RecipeSimpleViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeSimple(
    modifier: Modifier,
    recipeIndex : Int,
    recipe : RecipeSimpleViewModel,
    onItemClick : ((itemId : Int) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .background(MaterialTheme.colors.surface)
            .padding(bottom = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.secondary)

        ) {
            Text(
                text = "used in Recipe ${recipeIndex + 1}",
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier.padding(15.dp)
            )
        }
        if(recipe.craftedAt.isNotEmpty()) {
            Text(
                text = "Crafted At",
                modifier = Modifier.padding(top = 30.dp, start = 10.dp, bottom = 10.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                modifier = Modifier
                    .padding(start = 10.dp)
            ) {
                recipe.craftedAt.forEachIndexed { index, item ->
                    CraftedAtChip(
                        craftedAtIndex = index,
                        item = item,
                        onItemClick = onItemClick
                    )
                }
            }
        }
        Text(
            text = "Output",
            modifier = Modifier.padding(top = 20.dp, start = 10.dp, bottom = 10.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        recipe.outputs.forEach { itemAmount ->
            RecipeItemAmount(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                itemAmount = itemAmount,
                background = AppColor.rowBackgroundEven,
                onItemClick = onItemClick
            )
        }
        Text(
            text = "Input",
            modifier = Modifier.padding(top = 20.dp, start = 10.dp, bottom = 10.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        recipe.inputs.forEach { itemAmount ->
            RecipeItemAmount(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                itemAmount = itemAmount,
                background = AppColor.rowBackgroundEven,
                onItemClick = onItemClick
            )
        }
    }
}