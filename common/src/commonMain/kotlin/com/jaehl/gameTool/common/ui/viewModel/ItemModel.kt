package com.jaehl.gameTool.common.ui.viewModel

import com.jaehl.gameTool.common.ui.componets.ImageResource

data class ItemModel(
    val id : Int,
    val iconPath : ImageResource,
    val name : String
)