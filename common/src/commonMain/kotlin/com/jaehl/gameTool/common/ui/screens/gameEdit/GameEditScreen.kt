package com.jaehl.gameTool.common.ui.screens.gameEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.componets.*
import com.jaehl.gameTool.common.ui.screens.itemEdit.componets.ItemCategoryChip

class GameEditScreen(
    val gameId : Int?
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<GameEditScreenModel>()

        LaunchedEffect(gameId){
            screenModel.setup(
                GameEditScreenModel.Config(
                    gameId = gameId
                )
            )
        }

        LaunchedEffect(screenModel.closePageEvent.value){
            if(screenModel.closePageEvent.value){
                navigator.pop()
            }
        }

        GameEditPage(
            title = "GameEdit",
            viewModel = screenModel.viewModel.value,
            onBackClick = {
                screenModel.onBackClick()
            },
            onSaveClick = screenModel::onSaveClick,
            openDialogItemCategoryPicker = {
                screenModel.openDialogItemCategoryPicker()
            },
            onItemCategoryDelete = { itemCategory ->
                screenModel.onItemCategoryDelete(itemCategory)
            },
            onDelete = screenModel::onDelete,
            onNameChange = screenModel::onNameChange,
            onIconChange = screenModel::onIconChange,
            onBannerChange = screenModel::onBannerChange
        )

        val dialogConfig = screenModel.dialogConfig.value

        if(dialogConfig is GameEditScreenModel.DialogConfig.DialogSaveWarning){
            WarningDialog(
                title = "Unsaved changes",
                message = "You have unsaved changes, do you want to save changes?",
                positiveText = "Save",
                negativeText = "Discard",
                onPositiveClick = {
                    screenModel.closeDialog()
                    screenModel.onSaveClick(closePageAfter = true)
                },
                onNegativeClick = {
                    screenModel.closeDialog()
                    navigator.pop()
                }
            )
        }

        if(dialogConfig is GameEditScreenModel.DialogConfig.DialogItemCategoryPicker){
            ItemCategoryPickDialog(
                title = "Category",
                categoryList = dialogConfig.itemCategories,
                onCategoryClick = { itemCategory ->
                    screenModel.onItemCategoryAdd(itemCategory)
                    screenModel.closeDialog()
                },
                searchText = dialogConfig.searchText,
                onSearchTextChange = {searchText ->
                     screenModel.onDialogItemCategoryPickerSearchTextChange(searchText)
                },
                onAddNewCategoryClick = { name ->
                    screenModel.addNewItemCategory(name)
                    screenModel.closeDialog()
                },
                onClose = {
                    screenModel.closeDialog()
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameEditPage(
    title : String,
    viewModel: GameEditViewModel,
    onBackClick : () -> Unit,
    onSaveClick : () -> Unit,
    onDelete : () -> Unit,
    openDialogItemCategoryPicker : () -> Unit,
    onItemCategoryDelete : (itemCategory: ItemCategory) -> Unit,
    onNameChange : (value : String) -> Unit,
    onIconChange : (filePath : String) -> Unit,
    onBannerChange : (filePath : String) -> Unit
) {
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
                Button(
                    onClick = {
                        onSaveClick()
                    },
                    modifier = Modifier
                ) {
                    Text("Save")
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Card(
                modifier = Modifier
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .background(MaterialTheme.colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    StyledOutlinedTextField(
                        viewModel.name,
                        modifier = Modifier
                            .padding(top = 5.dp),
                        label = { Text("Name") },
                        enabled = !viewModel.pageLoading,
                        onValueChange = { value ->
                            onNameChange(value)
                        }
                    )

                    Text(text = "Item Categories", modifier = Modifier.padding(top = 20.dp))
                    FlowRow(modifier = Modifier, verticalArrangement = Arrangement.Center) {
                        viewModel.itemCategories.forEach { itemCategory ->
                            ItemCategoryChip(
                                itemCategory,
                                onItemCategoryDeleteClick = onItemCategoryDelete)
                        }
                        Button(
                            onClick = {
                                openDialogItemCategoryPicker()
                            },
                            modifier = Modifier
                        ) {
                            Text("Add")
                        }
                    }

                    ImageEdit(
                        modifier = Modifier
                            .padding(top = 10.dp),
                        title = "Icon",
                        viewModel.icon,
                        viewModel.iconError,
                        onIconChange = onIconChange,
                        width = 150.dp, height = 150.dp
                    )
                    ImageEdit(
                        modifier = Modifier
                            .padding(top = 10.dp),
                        title = "Banner",
                        viewModel.banner,
                        viewModel.bannerError,
                        onIconChange = onBannerChange,
                        width = 450.dp, height = 200.dp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        if(viewModel.showDelete) {
                            Button(
                                modifier = Modifier
                                    .padding(bottom = 10.dp, end = 10.dp)
                                    .align(alignment = Alignment.CenterEnd),
                                onClick = {
                                    onDelete()
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                            ) {
                                Text("Delete")
                            }
                        }
                    }

                }
            }
        }
    }
}