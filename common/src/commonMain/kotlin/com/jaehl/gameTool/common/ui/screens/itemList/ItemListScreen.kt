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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.componets.AppBar
import com.jaehl.gameTool.common.ui.componets.CustomVerticalScrollbar
import com.jaehl.gameTool.common.ui.componets.ItemIcon
import com.jaehl.gameTool.common.ui.screens.itemDetails.ItemDetailsScreen
import com.jaehl.gameTool.common.ui.screens.itemEdit.ItemEditScreen

class ItemListScreen(
    val gameId : Int
): Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel<ItemListScreenModel.Config, ItemListScreenModel>(
            arg = ItemListScreenModel.Config(gameId = gameId)
        )

        ItemListPage(
            items = screenModel.items,
            searchText = "",
            filterCategory = "",
            onBackClick = {
                navigator.pop()
            },
            onSearchTextChange = {},
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

//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Gray)
//        ) {
//            AppBar(
//                title = "ItemList",
//                backButtonEnabled = true,
//                onBackClick = {
//                    navigator.pop()
//                }
//            )
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//
//            ) {
//                Card(
//                    modifier = Modifier
//                        .width(300.dp)
//                        .height(400.dp)
//                        .align(Alignment.Center)
//                ) {
//                    Text("Test")
//                }
//            }
//        }

    }
}

@Composable
fun ItemListPage(
    items : List<ItemRowModel>,
    searchText : String,
    filterCategory : String,
    onBackClick : ()-> Unit,
    onSearchTextChange : (value : String) -> Unit,
    onItemClick : (itemId : Int) -> Unit,
    onItemEditClick : (itemId : Int?) -> Unit

) {
    //var isItemCategoryPickOpen by remember { mutableStateOf(false) }

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
                Button(
                    modifier = Modifier.padding(start = 20.dp),
                    onClick = {
                        //viewModel.createFromImages()
                    }
                ) {
                    Text("Create From Images")
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
                        value = filterCategory,
                        onValueChange = { },
                        label = { Text("Filter Category") }
                    )
                    Box(modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            //isItemCategoryPickOpen = true
                        })
                }
            }

            ItemList(items, onItemClick, onItemEditClick)
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