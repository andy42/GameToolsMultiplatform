package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.AppColor
import com.jaehl.gameTool.common.ui.viewModel.ItemModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemPickDialog(
    title: String,
    itemList : List<ItemModel>,
    isClearable : Boolean,
    onItemClick: (itemId : Int?) -> Unit,
    searchText : String,
    onSearchChange : ((String) -> Unit)?,
    showFilterCategoryPicker : Boolean = false,
    filterCategories : List<ItemCategory> = listOf(),
    filterCategory : ItemCategory = ItemCategory.Item_Category_ALL,
    onClose : () -> Unit,
    onCategoryFilterSelected : ((value : ItemCategory) -> Unit)? = null,
    onOpenCategoryFilter : (() -> Unit)? = null,
    onCloseCategoryFilter : (() -> Unit)? = null,
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .clickable(interactionSource = MutableInteractionSource(), indication = null, onClick = {})
        .background(AppColor.dialogBackground)) {
        Column(
            modifier = Modifier
                .width(500.dp)
                .fillMaxHeight()
                .padding(top = 20.dp, bottom = 20.dp)
                .align(Alignment.Center)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DialogTitleBar(
                title = title,
                onClose = onClose
            )
            if(isClearable) {
                Button(onClick = {
                    onItemClick(null)
                }) {
                    Text("Clear")
                }
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                if(onSearchChange != null) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            onSearchChange(it)
                        },
                        label = { Text("Search") },
                        modifier = Modifier
                            .padding(top = 10.dp, end = 10.dp)
                    )
                }
                if(onOpenCategoryFilter != null) {
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
                                onOpenCategoryFilter()
                            })
                        DropdownMenu(
                            expanded = showFilterCategoryPicker,
                            onDismissRequest = {
                                onCloseCategoryFilter?.invoke()
                            },
                            modifier = Modifier
                        ) {
                            filterCategories.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        onCategoryFilterSelected?.invoke(it)
                                    }
                                ) {
                                    Text(text = it.name)
                                }
                            }
                        }
                    }
                }
            }
            LazyColumn {
                itemsIndexed(itemList) { index, item ->
                    ItemPickerRow(index, item, onItemClick)
                }
            }
        }
    }
}

@Composable
fun ItemPickerRow(
    index : Int,
    item : ItemModel,
    onItemClick: (itemId : Int) -> Unit
){
    Row (
        modifier = Modifier
            .clickable {  onItemClick(item.id) }
            .background(if(index.mod(2) == 0) AppColor.rowBackgroundEven else AppColor.rowBackgroundOdd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemIcon(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            item.iconPath
        )
        Text(
            item.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
    }
}