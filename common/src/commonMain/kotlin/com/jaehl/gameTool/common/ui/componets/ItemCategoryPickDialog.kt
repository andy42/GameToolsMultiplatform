package com.jaehl.gameTool.common.ui.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.AppColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemCategoryPickDialog(
    title: String,
    categoryList : List<ItemCategory>,
    onCategoryClick: (ItemCategory) -> Unit,
    onClose : () -> Unit,
    searchText: String,
    onSearchTextChange : (searchText : String) -> Unit,
    onAddNewCategoryClick : ((name : String)->Unit)? = null,

){
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .clickable(interactionSource = MutableInteractionSource(), indication = null, onClick = {})
        .background(AppColor.dialogBackground)) {
        Column(
            modifier = Modifier
                .width(450.dp)
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
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        onSearchTextChange(it)
                    },
                    label = { Text("Search") },
                    modifier = Modifier
                )
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    onClick = {
                        onAddNewCategoryClick?.invoke(searchText)
                    },
                    enabled = (onAddNewCategoryClick != null)
                ) {
                    Text("Add New")
                }
            }
            LazyColumn {
                itemsIndexed(categoryList.filter { it.name.contains(searchText, ignoreCase = true) }) { index, category ->
                    ItemCategoryPickerRow(index, category, onCategoryClick)
                }
            }
        }
    }
}

@Composable
fun ItemCategoryPickerRow(
    index : Int,
    category : ItemCategory,
    onCategoryClick: (ItemCategory) -> Unit
){
    Row (
        modifier = Modifier
            .height(40.dp)
            .clickable {  onCategoryClick(category) }
            .background(if(index.mod(2) == 0) AppColor.rowBackgroundEven else AppColor.rowBackgroundOdd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            category.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
    }
}