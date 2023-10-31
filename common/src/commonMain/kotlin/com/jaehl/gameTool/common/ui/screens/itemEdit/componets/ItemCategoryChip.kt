package com.jaehl.gameTool.common.ui.screens.itemEdit.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jaehl.gameTool.common.data.model.ItemCategory

@Composable
fun ItemCategoryChip(
    enabled : Boolean,
    itemCategory : ItemCategory,
    onItemCategoryDeleteClick : (itemCategory : ItemCategory) -> Unit
){
    Row(
        modifier = Modifier
            .padding(top = 5.dp, end = 10.dp, bottom = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.secondary)
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = itemCategory.name,
            color = MaterialTheme.colors.onSecondary,
            modifier = Modifier
                .padding(start = 5.dp))
        IconButton(
            enabled = enabled,
            content = {
                Icon(Icons.Outlined.Delete, "delete", tint = MaterialTheme.colors.onSecondary)
            },
            onClick = {
                onItemCategoryDeleteClick(itemCategory)
            }
        )
    }
}