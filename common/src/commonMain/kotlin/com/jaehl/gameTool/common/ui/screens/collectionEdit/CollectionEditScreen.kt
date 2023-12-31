package com.jaehl.gameTool.common.ui.screens.collectionEdit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.componets.*
import com.jaehl.gameTool.common.ui.screens.collectionEdit.CollectionEditScreenModel.GroupViewModel
import com.jaehl.gameTool.common.ui.viewModel.ItemAmountViewModel

class CollectionEditScreen (
    val gameId : Int,
    val collectionId : Int?
) : Screen  {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<CollectionEditScreenModel>()

        LaunchedEffect(collectionId) {
            screenModel.setup(
                CollectionEditScreenModel.Config(
                    gameId = gameId,
                    collectionId = collectionId
                )
            )
        }

        LaunchedEffect(screenModel.closePageEvent.value){
            if(screenModel.closePageEvent.value){
                navigator.pop()
            }
        }

        Box {
            CollectionEditPage(
                loading = screenModel.pageLoading.value,
                title = screenModel.title.value,
                collectionName = screenModel.collectionName.value,
                onBackClick = {
                    screenModel.onBackClick()
                },
                onSaveClick = {
                    screenModel.save()
                },
                onAddNewGroupClick = {
                    screenModel.onAddNewGroupClick()
                },
                onCollectionTextChange = { value ->
                    screenModel.onCollectionTextChange(value)
                },
                groupList = screenModel.groupList,

                onGroupNameChange = { groupId, value ->
                    screenModel.onGroupNameChange(
                        groupId = groupId,
                        value = value
                    )
                },
                onRemoveGroupClick = { groupId ->
                    screenModel.onRemoveGroupClick(groupId = groupId)
                },
                onItemAmountChange = { groupId, itemId, amount ->
                    screenModel.onItemAmountChange(
                        groupId = groupId,
                        itemId = itemId,
                        value = amount
                    )
                },
                onRemoveItemClick = { groupId, itemId ->
                    screenModel.onRemoveItemClick(
                        groupId = groupId,
                        itemId = itemId
                    )
                },
                onAddItemClick = { groupId ->
                    screenModel.openDialogItemPicker(
                        groupId = groupId
                    )
                }
            )
        }

        val dialogConfig = screenModel.dialogConfig.value

        if(dialogConfig is CollectionEditScreenModel.DialogConfig.DialogSaveWarning){
            WarningDialog(
                title = "Unsaved changes",
                message = "You have unsaved changes, do you want to save changes?",
                positiveText = "Save",
                negativeText = "Discard",
                onPositiveClick = {
                    screenModel.closeDialog()
                    screenModel.save(closeAfter = true)
                },
                onNegativeClick = {
                    screenModel.closeDialog()
                    navigator.pop()
                }
            )
        }

        if(dialogConfig is CollectionEditScreenModel.DialogConfig.DialogItemPicker){
            ItemPickDialog(
                title = "Item Picker",
                itemList = screenModel.itemModels
                    .filter { itemModel ->
                        itemModel.name.contains(
                            dialogConfig.searchText ,
                            ignoreCase = true)
                    }
                    .filter {
                        if(dialogConfig.itemCategoryFilter == ItemCategory.Item_Category_ALL){
                            true
                        }
                        else {
                            it.categories.contains(dialogConfig.itemCategoryFilter)
                        }
                    }
                ,
                isClearable = false,
                onItemClick = {itemId : Int? ->
                    if(itemId != null) {
                        screenModel.onAddItemClick(
                            groupId = dialogConfig.groupId,
                            itemId = itemId
                        )
                    }
                    screenModel.closeDialog()
                },
                searchText = dialogConfig.searchText,
                onSearchChange = { searchText ->
                    screenModel.dialogItemPickerSearchTextChange(value = searchText)
                },
                showFilterCategoryPicker = dialogConfig.showItemCategoryPicker,
                filterCategories = dialogConfig.itemCategories,
                filterCategory = dialogConfig.itemCategoryFilter,
                onCategoryFilterSelected = {
                    screenModel.dialogItemPickerFilterCategoryChange(value = it)
                },
                onOpenCategoryFilter = {
                    screenModel.dialogItemPickerFilterCategoryPickerOpen()
                },
                onCloseCategoryFilter = {
                    screenModel.dialogItemPickerFilterCategoryPickerClose()
                },
                onClose = {
                    screenModel.closeDialog()
                }
            )
        }
    }
}

@Composable
fun CollectionEditPage(
    loading : Boolean,
    title : String,
    collectionName : TextFieldValue,
    onBackClick : () -> Unit,
    onSaveClick : () -> Unit,
    onAddNewGroupClick : () -> Unit,
    onCollectionTextChange : (value : String) -> Unit,
    groupList : List<GroupViewModel>,

    onGroupNameChange : (groupId : Int, value : String) -> Unit,
    onRemoveGroupClick : (groupId : Int) -> Unit,
    onItemAmountChange : (groupId : Int, itemId : Int, amount : String) -> Unit,
    onRemoveItemClick : (groupId : Int, itemId : Int) -> Unit,
    onAddItemClick : (groupId : Int) -> Unit
) {
    val state : ScrollState = rememberScrollState()
    Column (
        modifier = Modifier
    ) {
        AppBar(
            title = title,
            enabledBackButton = !loading,
            showBackButton = true,
            onBackClick = {
                onBackClick()
            },
            actions = {
                Button(
                    enabled = !loading,
                    onClick = {
                        onSaveClick()
                    },
                    modifier = Modifier
                ) {
                    Text("Save")
                }
            }
        )
        CustomLinearProgressIndicator(loading)
        Box(Modifier.fillMaxWidth()){
            Column(modifier = Modifier
                .padding(20.dp)
                .verticalScroll(state)
            ) {
                StyledOutlinedTextField(
                    collectionName,
                    enabled = !loading,
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 20.dp),
                    label = { Text("Crafting list title") },
                    //enabled = !screenModel.pageLoading.value,
                    onValueChange = { value ->
                        onCollectionTextChange(value)
                    },

                )
                groupList.forEach { groupViewModel ->
                    Group(
                        enabled = !loading,
                        groupViewModel = groupViewModel,
                        onGroupNameChange = onGroupNameChange,
                        onRemoveGroupClick = onRemoveGroupClick,
                        onItemAmountChange = onItemAmountChange,
                        onRemoveItemClick = onRemoveItemClick,
                        onAddItemClick = onAddItemClick
                    )
                }
                Button(
                    enabled = !loading,
                    onClick = {
                        onAddNewGroupClick()
                    },
                    modifier = Modifier
                ) {
                    Text("Add Group")
                }
            }
            CustomVerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                scrollState = state
            )
        }
    }
}

@Composable
fun Group(
    enabled : Boolean,
    groupViewModel : GroupViewModel,
    onGroupNameChange : (groupId : Int, value : String) -> Unit,
    onRemoveGroupClick : (groupId : Int) -> Unit,
    onItemAmountChange : (groupId : Int, itemId : Int, amount : String) -> Unit,
    onRemoveItemClick : (groupId : Int, itemId : Int) -> Unit,
    onAddItemClick : (groupId : Int) -> Unit
){
    Box(
        modifier = Modifier
            .padding(bottom = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.secondary)
                    .padding(2.dp)
                    .background(Color.White)
            ) {

                OutlinedTextField(
                    enabled = enabled,
                    value = groupViewModel.name.value,
                    onValueChange = { onGroupNameChange(groupViewModel.id, it) },
                    label = { Text("Group Name") },
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1.0f)
                )
                IconButton(
                    enabled = enabled,
                    content = {
                        Icon(Icons.Outlined.Delete, "delete", tint = Color.Black)
                    },
                    onClick = {
                        onRemoveGroupClick(groupViewModel.id)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

            groupViewModel.itemList.values.forEachIndexed { index, itemViewModel ->
                ItemRecipeRow(
                    enabled,
                    index,
                    groupViewModel.id,
                    itemViewModel,
                    onItemAmountChange,
                    onRemoveItemClick
                )
            }
            Button(
                enabled = enabled,
                onClick = {
                    onAddItemClick(groupViewModel.id)
                }
            ) {
                Text("Add Item")
            }
        }
    }
}

@Composable
fun ItemRecipeRow(
    enabled : Boolean,
    index : Int,
    groupId : Int,
    itemViewModel : ItemAmountViewModel,
    onItemAmountChange : (groupId : Int, itemId : Int, amount : String) -> Unit,
    removeItem : (groupId : Int, itemId : Int) -> Unit,
){
    Row (
        modifier = Modifier
            .background(if(index.mod(2) == 0) AppColor.rowBackgroundEven else AppColor.rowBackgroundOdd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            modifier = Modifier.width(200.dp).padding(top = 10.dp),
            onClick = { },
            border = BorderStroke(1.dp, MaterialTheme.colors.secondary),
        ){
            Row(
                //modifier = Modifier()
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemIcon(
                    Modifier
                        .width(40.dp)
                        .height(40.dp),
                    itemViewModel.itemModel.iconPath
                )
                Text(
                    itemViewModel.itemModel.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
            }
        }

        OutlinedTextField(
            enabled = enabled,
            modifier = Modifier
                .requiredWidth(90.dp)
                .padding(start = 10.dp),
            value = if(itemViewModel.amount == 0) "" else  itemViewModel.amount.toString(),
            onValueChange = { value ->
                onItemAmountChange(groupId, itemViewModel.itemModel.id, value.filter { it.isDigit() })
            },
            label = { Text("amount") }
        )

        IconButton(
            enabled = enabled,
            modifier = Modifier
                .requiredWidth(IntrinsicSize.Max)
                .padding(start = 10.dp)
            ,
            content = {
                Icon(Icons.Outlined.Delete, "delete", tint = Color.Black)
            },
            onClick = {
                removeItem(groupId, itemViewModel.itemModel.id)
            }
        )
    }
}