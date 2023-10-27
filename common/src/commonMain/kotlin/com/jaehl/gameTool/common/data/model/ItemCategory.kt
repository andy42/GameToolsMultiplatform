package com.jaehl.gameTool.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemCategory(
    val id : Int,
    val name : String
)
