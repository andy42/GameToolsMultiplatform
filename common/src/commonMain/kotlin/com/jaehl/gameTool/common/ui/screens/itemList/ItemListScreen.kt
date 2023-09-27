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
import androidx.compose.runtime.LaunchedEffect
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

        val searchText = remember { mutableStateOf("") }
        val categoryFilter = remember { mutableStateOf(ItemListScreenModel.Item_Category_ALL) }
        val isItemCategoryPickerOpen = remember { mutableStateOf(false) }

        ItemListPage(
            items = screenModel.items,
            searchText = searchText.value,
            filterCategory = categoryFilter.value,
            onBackClick = {
                navigator.pop()
            },
            onSearchTextChange = {
                searchText.value = it
            },
            onCategoryFilterClick = {
                isItemCategoryPickerOpen.value = true
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

        if(isItemCategoryPickerOpen.value){
            ItemCategoryPickDialog(
                title = "Category",
                categoryList = screenModel.itemCategories,
                onCategoryClick = { itemCategory ->
                    categoryFilter.value = itemCategory
                    isItemCategoryPickerOpen.value = false
                },
                onClose = {
                    isItemCategoryPickerOpen.value = false
                }
            )
        }
    }
}

@Composable
fun ItemListPage(
    items : List<ItemRowModel>,
    searchText : String,
    filterCategory : ItemCategory,
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
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    onItemEditClick(null)
                }) {
                    Text("Create New")
                }
            }
            Row(
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
                )
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
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
                        if(filterCategory == ItemListScreenModel.Item_Category_ALL){
                            true
                        }
                        else {
                            it.itemCategories.contains(filterCategory)
                        }
                    }
                ,
                onItemClick,
                onItemEditClick)
        }
    }
}

@Composable
fun ItemList(
    itemList : List<ItemRowModel>,
    onItemClick : (itemId : Int) -> Unit,
    onItemEditClick : (itemId : Int?) -> Unit
){
    Box {
        val state  = rememberLazyListState()
        LazyColumn(state = state) {
            itemsIndexed(itemList) { index, item ->
                ItemRow(index, item, onItemClick, onItemEditClick)
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
    onItemClick : (itemId : Int) -> Unit,
    onItemEditClick : (itemId : Int?) -> Unit
){
    Row (
        modifier = Modifier
            .clickable {  onItemClick(item.id) }
            .background(if(index.mod(2) == 0) AppColor.rowBackgroundEven else AppColor.rowBackgroundOdd),
        verticalAlignment = Alignment.CenterVertically
    ){
        ItemIcon(item.imageResource)
        Text(
            item.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
        IconButton(content = {
            Icon(Icons.Outlined.Edit, "edit", tint = Color.Black)
        }, onClick = {
            onItemEditClick(item.id)
        })
    }
}