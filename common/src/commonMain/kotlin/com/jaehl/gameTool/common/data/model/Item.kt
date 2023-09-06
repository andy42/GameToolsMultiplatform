package com.jaehl.gameTool.common.data.model

data class Item(
    val id : Int,
    val game : Int,
    val name : String,
    val categories : List<ItemCategory>,
    val image : Int
)
