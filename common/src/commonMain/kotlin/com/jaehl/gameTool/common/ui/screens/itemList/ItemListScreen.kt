package com.jaehl.gameTool.common.ui.screens.itemList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.componets.CustomVerticalScrollbar
import com.jaehl.gameTool.common.ui.componets.ItemCategoryPickDialog
import com.jaehl.gameTool.common.ui.componets.ItemIcon
import com.jaehl.gameTool.common.ui.screens.itemDetails.ItemDetailsScreen
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditScreen

class ItemListScreen(
    val gameId : Int
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<ItemListScreenModel>()

        LifecycleEffect(
            onStarted = {
                screenModel.setup(
                    config = ItemListScreenModel.Config(gameId)
                )
            }
        )

        ItemListPage(
            items = screenModel.items,
            searchText = screenModel.searchText.value,
            filterCategory = screenModel.categoryFilter.value,
            showEditItems = screenModel.showEditItems.value,
            onBackClick = {
                navigator.pop()
            },
            onSearchTextChange = {
                screenModel.searchText.value = it
            },
            onCategoryFilterClick = {
                screenModel.openDialogItemCategoryPicker()
            },
            onItemClick = { itemId ->
                navigator.push(ItemDetailsScreen(
                    gameId = gameId,
                    itemId = itemId
                ))
            },
            onItemEditClick = { itemId ->
                navigator.push(ItemEditScreen(
                    gameId = gameId,
                    itemId = itemId
                ))
            }
        )

        val dialogConfig = screenModel.dialogConfig.value
        if(dialogConfig is ItemListScreenModel.DialogConfig.DialogItemCategoryPicker){
            ItemCategoryPickDialog(
                title = "Category",
                categoryList = dialogConfig.itemCategories,
                onCategoryClick = { itemCategory ->
                    screenModel.categoryFilter.value = itemCategory
                    screenModel.closeDialog()
                },
                searchText = dialogConfig.searchText,
                onSearchTextChange = {
                     screenModel.onDialogItemCategoryPickerSearchTextChange(it)
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
fun ItemListPage(
    items : List<ItemRowModel>,
    searchText : String,
    filterCategory : ItemCategory,
    showEditItems : Boolean,
    onBackClick : ()-> Unit,
    onSearchTextChange : (value : String) -> Unit,
    onCategoryFilterClick : () -> Unit,
    onItemClick : (itemId : Int) -> Unit,
    onItemEditClick : (itemId : Int?) -> Unit

) {
    Column(modifier = Modifier) {
        AppBar(
            title = "Items",
            backButtonEnabled = true,
            onBackClick = {
                onBackClick()
            }
        )

        Column(modifier = Modifier.padding(20.dp)) {
            if(showEditItems) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        onItemEditClick(null)
                    }) {
                        Text("Create New")
                    }
                }
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        onSearchTextChange(it)
                    },
                    label = { Text("Search") },
                    modifier = Modifier
                        .padding(top = 10.dp, end = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, end = 10.dp)
                ) {
                    OutlinedTextField(
                        value = filterCategory.name,
                        onValueChange = { },
                        label = { Text("Filter Category") }
                    )
                    Box(modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            onCategoryFilterClick()
                        })
                }
            }

            ItemList(
                items
                    .filter { it.name.contains(searchText, ignoreCase = true)}
                    .filter {
                        if(filterCategory == ItemCategory.Item_Category_ALL){
                            true
                        }
                        else {
                            it.itemCategories.contains(filterCategory)
                        }
                    }
                ,
                showEditItems,
                onItemClick,
                onItemEditClick)
        }
    }
}

@Composable
fun ItemList(
    itemList : List<ItemRowModel>,
    showEditItems : Boolean,
    onItemClick : (itemId : Int) -> Unit,
    onItemEditClick : (itemId : Int?) -> Unit
){
    Box {
        val state  = rememberLazyListState()
        LazyColumn(state = state) {
            itemsIndexed(itemList) { index, item ->
                ItemRow(index, item, showEditItems, onItemClick, onItemEditClick)
            }
        }
        CustomVerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            scrollState = state
        )
    }
}

@Composable
fun ItemRow(
    index : Int,
    item : ItemRowModel,
    showEditItems : Boolean,
    onItemClick : (itemId : Int) -> Unit,
    onItemEditClick : (itemId : Int?) -> Unit
){
    Row (
        modifier = Modifier
            .clickable {  onItemClick(item.id) }
            .background(if(index.mod(2) == 0) AppColor.rowBackgroundEven else AppColor.rowBackgroundOdd),
        verticalAlignment = Alignment.CenterVertically
    ){
        ItemIcon(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            item.imageResource
        )
        Text(
            item.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
        if(showEditItems) {
            IconButton(content = {
                Icon(Icons.Outlined.Edit, "edit", tint = Color.Black)
            }, onClick = {
                onItemEditClick(item.id)
            })
        }
    }
}