package com.jaehl.gameTool.common.ui.screens.itemDetails

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.componets.*
import com.jaehl.gameTool.common.ui.viewModel.ItemModel

class ItemDetailsScreen(
    private val gameId : Int,
    private val itemId : Int
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<ItemDetailsScreenModel.Config, ItemDetailsScreenModel>(
            arg = ItemDetailsScreenModel.Config(gameId = gameId, itemId = itemId)
        )

        ItemDetailsPage(
            itemId = itemId,
            itemInfo = screenModel.itemInfo.value,
            recipes = screenModel.recipes,
            onBackClick = {
                navigator.pop()
            },
            onItemClick = { clickedItemId ->
                navigator.push(ItemDetailsScreen(
                    gameId = gameId,
                    itemId = clickedItemId
                ))
            }
        )
    }
}

@Preview
@Composable
fun preview(){
    ItemDetailsPage(
        itemId=1,
        itemInfo = ItemInfoModel(
            name = "test",
            iconPath = ImageResource.ImageLocalResource(""),
            categories = listOf(
                "Resources", "CraftedResources"
            )
        ),
        recipes = listOf(),
        onBackClick = {},
        onItemClick = {}
    )
}

@Composable
fun ItemDetailsPage(
    itemId : Int,
    itemInfo : ItemInfoModel,
    recipes : List<RecipeViewModel>,
    onBackClick : ()-> Unit,
    onItemClick : (clickedItemId : Int) -> Unit
) {
    val state : ScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        AppBar(
            title = "ItemDetails",
            backButtonEnabled = true,
            onBackClick = {
                onBackClick()
            }
        )
        Box(
            Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state)
                    .padding(20.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                ) {
                    ItemQuickInfo(
                        itemInfo,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                recipes.forEachIndexed { index, recipeViewModel ->
                    Recipe(
                        recipeIndex = index,
                        recipe = recipeViewModel,
                        onItemClick = { clickedItemId ->
                            if (clickedItemId != itemId) {
                                onItemClick(clickedItemId)
                            }
                        },
                        onCollapseListToggle = {},
                        onShowBaseCraftingToggle = {}
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Recipe(
    recipeIndex : Int,
    recipe : RecipeViewModel,
    onItemClick : (clickedItemId : Int) -> Unit,
    onCollapseListToggle : (recipeIndex : Int) -> Unit,
    onShowBaseCraftingToggle : (recipeIndex : Int) -> Unit,
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .background(MaterialTheme.colors.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary)

            ) {
                Text(
                    text = "Recipe ${recipeIndex + 1}",
                    modifier = Modifier.padding(15.dp)
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                ) {
                    IconButton(
                        modifier = Modifier,
                        content = {
                            Icon(
                                Icons.Outlined.ArrowDropDown,
                                "Edit",
                                tint = if (recipe.collapseList) Color.White else Color.Black
                            )
                        },
                        onClick = {
                            onCollapseListToggle(recipeIndex)
                        }
                    )
                    IconButton(
                        modifier = Modifier,
                        content = {
                            Icon(
                                Icons.Outlined.List,
                                "Edit",
                                tint = if (recipe.showBaseCrafting) Color.White else Color.Black
                            )
                        },
                        onClick = {
                            onShowBaseCraftingToggle(recipeIndex)
                        }
                    )
                }

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



            if(recipe.node.itemAmount.amount != 1) {
                PageSpacer(Modifier.padding(top= 20.dp))
                Text(
                    text = "Output",
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp, bottom = 10.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                RecipeItemAmount(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    itemAmount = recipe.node.itemAmount,
                    background = AppColor.rowBackgroundEven,

                    )

                if (recipe.node.byProducts.isNotEmpty()) {

                    PageSpacer(Modifier.padding(top = 20.dp))

                    Text(
                        text = "By-Product",
                        modifier = Modifier.padding(top = 20.dp, start = 10.dp, bottom = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    recipe.node.byProducts.forEach {
                        RecipeItemAmount(
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                            itemAmount = it,
                            background = AppColor.rowBackgroundSecondEven,
                        )
                    }
                }
                PageSpacer(Modifier.padding(top= 20.dp))

                Text(
                    text = "Input",
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp, bottom = 10.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            RecipeNodes(
                Modifier.padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
                recipe.collapseList,
                recipe.node.inputs,
                onItemClick = onItemClick,
                onRecipeChange = { item ->

                }
            )
        }
    }
}


@Composable
fun CraftedAtChip(
    craftedAtIndex : Int,
    item : ItemModel,
    onItemClick : ((itemId : Int) -> Unit)? = null
){
    Row(
        modifier = Modifier
            .padding(top = 5.dp, end = 10.dp, bottom = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.primary)
            .clickable {
                onItemClick?.invoke(item.id)
            }
            .padding(start = 10.dp, top = 5.dp, bottom = 5.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        ItemIcon(
            item.iconPath,
            modifier = Modifier
        )
        Text(
            text = item.name,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .padding(start = 5.dp)
        )
    }
}

@Composable
fun ItemQuickInfo(item : ItemInfoModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(250.dp)
            .background(MaterialTheme.colors.primary)
    ) {
        ItemQuickInfoheading(item.name)
        item.iconPath?.let { iconPath ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 1.dp, end = 1.dp)
                    .background(MaterialTheme.colors.surface)
            ) {
                ItemIcon(
                    iconPath,
                    modifier = Modifier
                        .align(Alignment.Center),
                    size = 100.dp
                )
            }
        }
        ItemQuickInfoheading("General")
        ItemQuickInfoTitleValue("Categories", item.categories.joinToString(", "))
        //ItemQuickInfoTitleValue("Tech Tier", item.techTier.toString())
//        if(item.craftedAt != null) {
//            ItemQuickInfoTitleValue("Crafted at", item.craftedAt)
//        }
    }
}

@Composable
fun ItemQuickInfoheading(text : String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .align(Alignment.Center),
        )
    }
}

@Composable
fun ItemQuickInfoTitleValue(title : String, value : String) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 1.dp, end = 1.dp, bottom = 1.dp)
            .background(MaterialTheme.colors.surface)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .width(100.dp)
                .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
        )
        Text(
            text = value,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
        )
    }
}