package com.jaehl.gameTool.common.ui.screens.collectionDetails

//import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
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
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel
import com.jaehl.gameTool.common.ui.viewModel.RecipeDisplayType

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
            loading = screenModel.pageLoading.value,
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
            onRecipeChangeClick = {itemId, groupId ->
                screenModel.onRecipeChangeClick(itemId,groupId)
            }
        )

        val dialogViewModel = screenModel.dialogViewModel.value
        if(dialogViewModel is CollectionDetailsScreenModel.RecipeSettingsDialog ){
            RecipeSettingsDialog(
                title = "Recipe Settings",
                recipeSettings = dialogViewModel.settings,
                onClose = {
                    screenModel.closeDialog()
                },
                onRecipeSettingsChange = {
                    screenModel.onRecipeSettingsChange(
                        dialogViewModel.groupId,
                        it
                    )
                }
            )
        }

        if(dialogViewModel is CollectionDetailsScreenModel.RecipePickerDialog ){
            RecipePickerDialog(
                title = "Pick Recipe",
                recipePickerData = dialogViewModel.recipePickerData,
                onClose = {
                    screenModel.closeDialog()
                },
                onRecipeClick = { recipeId ->
                    screenModel.onRecipePickerSelectedClick(dialogViewModel, recipeId)
                },
                onRecipeConfirmClick = {
                    screenModel.onGroupItemRecipeChanged(
                        itemId = dialogViewModel.itemId,
                        groupId = dialogViewModel.groupId,
                        recipeId = dialogViewModel.recipePickerData.selectedRecipeId
                    )
                }

            )
        }

        ErrorDialog(dialogViewModel, onClose = screenModel::closeDialog)
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
    loading : Boolean,
    title : String,
    groupsList : List<CollectionDetailsScreenModel.GroupsViewModel>,
    onBackClick : () -> Unit,
    onEditClick : () -> Unit,
    onItemClick : (clickedItemId : Int) -> Unit,
    onRecipeSettingDialogStateClick : (groupId : Int) -> Unit,
    onRecipeChangeClick : (itemId : Int, groupId : Int) -> Unit
){
    val state : ScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        AppBar(
            title = title,
            showBackButton = true,
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
        CustomLinearProgressIndicator(loading)
        Box(
            Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(state)
                    .padding(20.dp)
            ) {
                groupsList.sortedBy { it.id }.forEachIndexed { index, groupsViewModel ->
                    Section(
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                        group = groupsViewModel,
                        onItemClick = onItemClick,
                        onRecipeSettingDialogStateClick = onRecipeSettingDialogStateClick,
                        onRecipeChangeClick = onRecipeChangeClick
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
    onRecipeSettingDialogStateClick : (groupId : Int) -> Unit,
    onRecipeChangeClick : (itemId : Int, groupId : Int) -> Unit
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
            group.itemList.forEach{ itemViewModel ->
                Item(itemViewModel = itemViewModel, groupId = group.id, onItemClick, onRecipeChangeClick)
            }
        }
        if(group.recipeSettings.displayType == RecipeDisplayType.FlattenedList) {
            //TODO("Add FlattenedList")
        }
        else {
            RecipeNodes(
                Modifier.padding(top = 10.dp, bottom = 10.dp),
                group.recipeSettings.collapseIngredients,
                if(group.recipeSettings.displayType == RecipeDisplayType.BaseItems) group.baseNodes else group.nodes,
                onItemClick = onItemClick,
                onRecipeChange = { itemId ->
                    onRecipeChangeClick(itemId, group.id)
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Item(
    itemViewModel : ItemAmountViewModel,
    groupId: Int,
    onItemClick : (itemId : Int) -> Unit,
    onItemRecipeChangeClick : (itemId : Int, groupId : Int) -> Unit,
){
    Column(
        modifier = Modifier
            .padding(end = 10.dp, top = 10.dp)

            .background(Color.White)
            .combinedClickable (
                onClick = {
                    onItemClick(itemViewModel.itemModel.id)
                },
                onLongClick = {
                    onItemRecipeChangeClick(itemViewModel.itemModel.id, groupId)
                }
            )
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