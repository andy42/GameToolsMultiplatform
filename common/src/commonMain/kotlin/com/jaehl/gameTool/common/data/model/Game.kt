package com.jaehl.gameTool.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id : Int,
    val name : String,
    val itemCategories : List<ItemCategory>,
    val icon : Int,
    val banner : Int
)
