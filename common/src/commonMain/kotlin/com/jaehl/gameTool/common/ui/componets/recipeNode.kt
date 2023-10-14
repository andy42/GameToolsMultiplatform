package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.data.model.ItemRecipeNode
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel

private fun backgroundColor(sectionIndex : Int) : Color {
    return if(sectionIndex.mod(2) == 0) AppColor.rowBackgroundEven else AppColor.rowBackgroundOdd
}

private fun byProductsBackgroundColor(sectionIndex : Int) : Color {
    return if(sectionIndex.mod(2) == 0) AppColor.rowBackgroundSecondEven else AppColor.rowBackgroundSecondOdd
}

@Composable
fun RecipeNodes(
    modifier: Modifier,
    collapseList : Boolean,
    recipeNodes : List<ItemRecipeNode>,
    depth : Int = 0,
    onItemClick : ((itemId : Int) -> Unit)? = null,
    onRecipeChange : ((itemId : Int) -> Unit)? = null
) {
    Column(
        modifier = modifier
    ) {
        recipeNodes.forEachIndexed { index, itemRecipeNode ->
            RecipeNode(
                modifier = Modifier,
                collapseList = collapseList,
                recipeNode = itemRecipeNode,
                depth = depth,
                sectionIndex = index,
                onItemClick = onItemClick,
                onRecipeChange = onRecipeChange
            )
        }
    }
}

@Composable
fun RecipeNode(
    modifier: Modifier,
    collapseList : Boolean,
    recipeNode : ItemRecipeNode,
    depth : Int = 0,
    sectionIndex : Int? = null,
    onItemClick : ((itemId : Int) -> Unit)? = null,
    onRecipeChange : ((itemId : Int) -> Unit)? = null
) {
    Column {

        RecipeItemAmount(
            Modifier,
            recipeNode.itemAmount,
            recipeNode.recipeCount,
            depth,
            backgroundColor(sectionIndex ?: -1),
            onItemClick,
            onRecipeChange
        )

        if(!collapseList) {
            recipeNode.byProducts.forEachIndexed { index, itemAmount ->
                RecipeItemAmount(
                    Modifier,
                    itemAmount,
                    0,
                    depth + 1,
                    byProductsBackgroundColor(sectionIndex ?: -1),
                    onItemClick,
                    onRecipeChange
                )
            }
            recipeNode.inputs.forEachIndexed { index, recipeNode ->
                RecipeNode(
                    Modifier,
                    collapseList,
                    recipeNode,
                    depth + 1,
                    sectionIndex ?: index,
                    //backgroundColor(sectionIndex),
                    onItemClick = onItemClick,
                    onRecipeChange = onRecipeChange
                )

            }
        }
    }
}

@Composable
fun RecipeItemAmount(
    modifier : Modifier,
    itemAmount : ItemAmountViewModel,
    recipeCount : Int = 0,
    depth : Int = 0,
    background : Color,
    onItemClick : ((itemId : Int) -> Unit)? = null,
    onRecipeChange : ((itemId : Int) -> Unit)? = null
) {
    Row (
        modifier = modifier
            //.padding(start = 30.dp*depth),
            .background(background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..depth){
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .padding(start = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(AppColor.recipeCircle)
                )
            }
        }
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
                .height(40.dp)
                //.background(if(alternativeRecipe) R.Color.debugGreen else R.Color.transparent)
                .clickable {
                    onItemClick?.invoke(itemAmount.itemModel.id)
                },
            itemAmount.itemModel.iconPath,
        )
        Text(
            itemAmount.itemModel.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
        if(recipeCount > 0) {
            IconButton(content = {
                Icon(Icons.Outlined.Edit, "Change recipe", tint = MaterialTheme.colors.onSurface)
            }, onClick = {
                onRecipeChange?.invoke(itemAmount.itemModel.id)
            })
        }
    }
}