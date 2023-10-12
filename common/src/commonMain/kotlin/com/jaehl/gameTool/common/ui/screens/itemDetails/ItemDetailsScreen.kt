package com.jaehl.gameTool.common.ui.screens.itemDetails

//import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.componets.*
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditScreen
import com.jaehl.gameTool.common.ui.viewModel.ItemModel
import kotlinx.coroutines.launch

class ItemDetailsScreen(
    private val gameId : Int,
    private val itemId : Int
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<ItemDetailsScreenModel>()
        val scrollState : ScrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(itemId){
            screenModel.update(
                config = ItemDetailsScreenModel.Config(
                    gameId = gameId,
                    itemId = itemId),
                ifItemChanged = true
            )
            coroutineScope.launch {
                scrollState.animateScrollTo(0)
            }
        }

        LifecycleEffect(
            onStarted = {
                screenModel.update(
                    ItemDetailsScreenModel.Config(
                        gameId = gameId,
                        itemId = itemId)
                )
            }
        )

        ItemDetailsPage(
            itemId = itemId,
            itemInfo = screenModel.itemInfo.value,
            recipes = screenModel.recipeModels,
            showEditItems = screenModel.showEditItems.value,
            scrollState = scrollState,
            onBackClick = {
                navigator.pop()
            },
            onItemClick = { clickedItemId ->
                navigator.push(ItemDetailsScreen(
                    gameId = gameId,
                    itemId = clickedItemId
                ))
            },
            onEditClick = {
                navigator.push(
                    ItemEditScreen(
                        gameId = gameId,
                        itemId = itemId
                    )
                )
            },
            onRecipeSettingsClick = {
                screenModel.onRecipeSettingClick(it)
            }
        )

        if(screenModel.recipeSettingDialogState.value is ItemDetailsScreenModel.RecipeSettingDialogState.Open ){
            val recipeSettingDialogState = screenModel.recipeSettingDialogState.value as ItemDetailsScreenModel.RecipeSettingDialogState.Open
            RecipeSettingsDialog(
                title = "Recipe Settings",
                recipeSettings = recipeSettingDialogState.recipeSettings,
                onClose = {
                    screenModel.onRecipeSettingDialogStateClose()
                },
                onRecipeSettingsChange = {
                    screenModel.onRecipeSettingsChange(
                        recipeSettingDialogState.recipeId,
                        it
                    )
                }
            )
        }
    }
}

//@Preview
//@Composable
//fun preview(){
//    ItemDetailsPage(
//        itemId=1,
//        itemInfo = ItemInfoModel(
//            name = "test",
//            iconPath = ImageResource.ImageLocalResource(""),
//            categories = listOf(
//                "Resources", "CraftedResources"
//            )
//        ),
//        recipes = listOf(),
//        onBackClick = {},
//        onItemClick = {}
//    )
//}

@Composable
fun ItemDetailsPage(
    itemId : Int,
    itemInfo : ItemInfoModel,
    recipes : List<RecipeViewModel>,
    showEditItems : Boolean,
    scrollState : ScrollState,
    onBackClick : ()-> Unit,
    onItemClick : (clickedItemId : Int) -> Unit,
    onEditClick : () -> Unit,
    onRecipeSettingsClick : (recipeId : Int) -> Unit
) {
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
            },
            actions = {
                if(showEditItems) {
                    IconButton(content = {
                        Icon(Icons.Outlined.Edit, "Edit", tint = MaterialTheme.colors.onPrimary)
                    }, onClick = {
                        onEditClick()
                    })
                }
            }
        )
        Box(
            Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
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
                        onRecipeSettingsClick = onRecipeSettingsClick
                    )
                }
            }
            CustomVerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                scrollState = scrollState
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
    onRecipeSettingsClick : (recipeId : Int) -> Unit
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
                    .background(MaterialTheme.colors.secondary)

            ) {
                Text(
                    text = "Recipe ${recipeIndex + 1}",
                    color = MaterialTheme.colors.onSecondary,
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
                                Icons.Outlined.Settings,
                                "Edit",
                                tint = MaterialTheme.colors.onSecondary
                            )
                        },
                        onClick = {
                            onRecipeSettingsClick(recipe.id)
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
                recipe.recipeSettings.collapseIngredients,
                if(recipe.recipeSettings.showBaseIngredients) recipe.baseIngredients else recipe.node.inputs,
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
            .background(MaterialTheme.colors.secondary)
            .clickable {
                onItemClick?.invoke(item.id)
            }
            .padding(start = 10.dp, top = 5.dp, bottom = 5.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        ItemIcon(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            item.iconPath,
        )
        Text(
            text = item.name,
            color = MaterialTheme.colors.onSecondary,
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
            .background(MaterialTheme.colors.secondary)
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
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .align(Alignment.Center),
                    iconPath
                )
            }
        }
        ItemQuickInfoheading("General")
        ItemQuickInfoTitleValue("Categories", item.categories.joinToString(", "))
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
            color = MaterialTheme.colors.onSecondary,
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