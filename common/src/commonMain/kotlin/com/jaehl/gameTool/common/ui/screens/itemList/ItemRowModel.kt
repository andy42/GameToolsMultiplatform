package com.jaehl.gameTool.common.ui.screens.itemList

import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.componets.ImageResource

data class ItemRowModel(
    val id : Int,
    val name : String,
    val itemCategories : List<ItemCategory>,
    val imageResource : ImageResource
)
