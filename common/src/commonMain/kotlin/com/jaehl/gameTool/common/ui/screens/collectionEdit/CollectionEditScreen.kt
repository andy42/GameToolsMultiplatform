package com.jaehl.gameTool.common.ui.screens.collectionEdit

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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

        var isItemPickOpen = remember { mutableStateOf(false) }
        var itemPickerSearchText = remember {mutableStateOf("") }
        var itemPickerGroupId = remember { mutableStateOf(-1) }

        LaunchedEffect(collectionId) {
            screenModel.setup(
                CollectionEditScreenModel.Config(
                    gameId = gameId,
                    collectionId = collectionId
                )
            )
        }
        Box {
            CollectionEditPage(
                title = screenModel.title.value,
                collectionName = screenModel.collectionName.value,
                onBackClick = {
                    navigator.pop()
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
                    itemPickerGroupId.value = groupId
                    isItemPickOpen.value = true
                }
            )
        }
        if(isItemPickOpen.value) {
            ItemPickDialog(
                title = "",
                itemList = screenModel.itemModels.filter { itemModel ->
                    itemModel.name.contains(
                        itemPickerSearchText.value ,
                        ignoreCase = true) },
                isClearable = false,
                onItemClick = {itemId : Int? ->
                    if(itemId != null) {
                        screenModel.onAddItemClick(
                            groupId = itemPickerGroupId.value,
                            itemId = itemId
                        )
                    }
                    isItemPickOpen.value = false
                    itemPickerSearchText.value = ""
                },
                searchText = itemPickerSearchText.value,
                onSearchChange = { searchText ->
                    itemPickerSearchText.value = searchText
                },
                onClose = {
                    isItemPickOpen.value = false
                    itemPickerSearchText.value = ""
                }
            )
        }
    }
}

@Composable
fun CollectionEditPage(
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
        Box(Modifier.fillMaxWidth()){
            Column(modifier = Modifier
                .padding(20.dp)
                .verticalScroll(state)
            ) {
                StyledOutlinedTextField(
                    collectionName,
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
                        groupViewModel = groupViewModel,
                        onGroupNameChange = onGroupNameChange,
                        onRemoveGroupClick = onRemoveGroupClick,
                        onItemAmountChange = onItemAmountChange,
                        onRemoveItemClick = onRemoveItemClick,
                        onAddItemClick = onAddItemClick
                    )
                }
                Button(
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
                    .background(MaterialTheme.colors.primary)
                    .padding(2.dp)
                    .background(Color.White)
            ) {

                OutlinedTextField(
                    value = groupViewModel.name.value,
                    onValueChange = { onGroupNameChange(groupViewModel.id, it) },
                    label = { Text("Group Name") },
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1.0f)
                )
                IconButton(
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
                    index,
                    groupViewModel.id,
                    itemViewModel,
                    onItemAmountChange,
                    onRemoveItemClick
                )
            }
            Button(
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
            border = BorderStroke(1.dp, Color.Red),
        ){
            Row(
                //modifier = Modifier()
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemIcon(itemViewModel.itemModel.iconPath)
                Text(
                    itemViewModel.itemModel.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .padding(start = 10.dp),
            value = if(itemViewModel.amount == 0) "" else  itemViewModel.amount.toString(),
            onValueChange = { value ->
                onItemAmountChange(groupId, itemViewModel.itemModel.id, value.filter { it.isDigit() })
            },
            label = { Text("amount") }
        )
        IconButton(content = {
            Icon(Icons.Outlined.Delete, "delete", tint = Color.Black)
        }, onClick = {
            removeItem(groupId, itemViewModel.itemModel.id)
        })
    }
}