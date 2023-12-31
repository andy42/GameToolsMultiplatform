package com.jaehl.gameTool.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id : Int = -1,
    val gameId : Int = -1,
    val craftedAt : List<Int> = listOf(),
    val input : List<ItemAmount> = listOf(),
    val output : List<ItemAmount> = listOf()
)
