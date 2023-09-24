package com.jaehl.gameTool.common.ui.viewModel
import com.jaehl.gameTool.common.data.model.ItemCategory
import com.jaehl.gameTool.common.ui.componets.ImageResource

data class ItemModel(
    val id : Int,
    val iconPath : ImageResource,
    val categories : List<ItemCategory>,
    val name : String
)