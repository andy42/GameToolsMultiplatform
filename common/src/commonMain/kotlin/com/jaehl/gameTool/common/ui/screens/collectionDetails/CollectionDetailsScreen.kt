package com.jaehl.gameTool.common.ui.screens.collectionDetails

//import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.componets.*
import com.jaehl.gameTool.common.ui.screens.collectionEdit.CollectionEditScreen
import com.jaehl.gameTool.common.ui.screens.itemDetails.ItemDetailsScreen
import com.jaehl.gameTool.common.ui.screens.itemDetails.ItemDetailsScreenModel
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel

class CollectionDetailsScreen(
    val gameId : Int,
    val collectionId : Int
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<CollectionDetailsScreenModel>()

        LifecycleEffect(
            onStarted = {
                screenModel.setup(
                    CollectionDetailsScreenModel.Config(
                        gameId = gameId,
                        collectionId = collectionId)
                )
            }
        )

        CollectionDetailsPage(
            title = screenModel.title.value,
            groupsList = screenModel.groups,
            onBackClick = {
                navigator.pop()
            },
            onEditClick = {
                navigator.push(
                    CollectionEditScreen(
                        gameId = gameId,
                        collectionId = collectionId
                    )
                )
            },
            onItemClick = { clickedItemId ->
                navigator.push(
                    ItemDetailsScreen(
                        gameId = gameId,
                        itemId = clickedItemId
                    )
                )
            },
            onRecipeSettingDialogStateClick = screenModel::onRecipeSettingDialogStateClick,
        )

        if(screenModel.recipeSettingDialogState.value is CollectionDetailsScreenModel.RecipeSettingDialogState.Open ){
            val recipeSettingDialogState = screenModel.recipeSettingDialogState.value as CollectionDetailsScreenModel.RecipeSettingDialogState.Open
            RecipeSettingsDialog(
                title = "Recipe Settings",
                recipeSettings = recipeSettingDialogState.settings,
                onClose = {
                    screenModel.onRecipeSettingDialogStateClose()
                },
                onRecipeSettingsChange = {
                    screenModel.onRecipeSettingsChange(
                        recipeSettingDialogState.groupId,
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
//    CollectionDetailsPage(
//        title = "Collection 1",
//        groupsList = listOf(
//            CollectionDetailsScreenModel.GroupsViewModel(
//                id = 1,
//                name = "Group 1",
//                collapseIngredientList = false,
//                showBaseCrafting = false,
//                itemList = listOf(
//                    ItemAmountViewModel(
//                        item = ItemModel(
//                            id = 1,
//                            iconPath = ImageResource.ImageLocalResource(
//                                ""
//                            ),
//                            name = "test"
//                        )
//                    )
//                ),
//                nodes = listOf(),
//                baseNodes = listOf()
//            ),
//        ),
//        onBackClick = {},
//        onItemClick = {},
//        onCollapseIngredientsClick = {},
//        showBaseCrafting = {}
//    )
//}

@Composable
fun CollectionDetailsPage(
    title : String,
    groupsList : List<CollectionDetailsScreenModel.GroupsViewModel>,
    onBackClick : () -> Unit,
    onEditClick : () -> Unit,
    onItemClick : (clickedItemId : Int) -> Unit,
    onRecipeSettingDialogStateClick : (groupId : Int) -> Unit,
){
    val state : ScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        AppBar(
            title = title,
            backButtonEnabled = true,
            onBackClick = {
                onBackClick()
            },
            actions = {
                IconButton(content = {
                    Icon(Icons.Outlined.Edit, "Edit", tint = MaterialTheme.colors.onPrimary)
                }, onClick = {
                    onEditClick()
                })
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
                groupsList.forEachIndexed { index, groupsViewModel ->
                    Section(
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                        group = groupsViewModel,
                        onItemClick = onItemClick,
                        onRecipeSettingDialogStateClick = onRecipeSettingDialogStateClick
                    )
                }
            }
            CustomVerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                scrollState = state
            )
        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Section(
    modifier: Modifier,
    group : CollectionDetailsScreenModel.GroupsViewModel,
    onItemClick : (itemId : Int) -> Unit,
    onRecipeSettingDialogStateClick : (groupId : Int) -> Unit
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.secondary)
        ) {
            Text(
                text = group.name,
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
                        Icon(Icons.Outlined.Settings, "Settings", tint = MaterialTheme.colors.onSecondary)
                    },
                    onClick = {
                        onRecipeSettingDialogStateClick(
                            group.id
                        )
                    }
                )
            }

        }
        FlowRow {
            group.itemList.forEach{ item ->
                Item(item, onItemClick)
            }
        }
        RecipeNodes(
            Modifier.padding(top = 10.dp, bottom = 10.dp),
            group.recipeSettings.collapseIngredients,
            if(group.recipeSettings.showBaseIngredients) group.baseNodes else group.nodes,
            onItemClick = onItemClick
        )
    }
}

@Composable
fun Item(itemViewModel : ItemAmountViewModel, onItemClick : (itemId : Int) -> Unit){
    Column(
        modifier = Modifier
            .padding(end = 10.dp, top = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .clickable {
                onItemClick(itemViewModel.itemModel.id)
            }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(

            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemIcon(
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .padding(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 10.dp),
                imageResource = itemViewModel.itemModel.iconPath
            )
            Text(
                itemViewModel.amount.toString(),
                fontSize = 30.sp
            )
        }
        Text(text = itemViewModel.itemModel.name)
    }
}