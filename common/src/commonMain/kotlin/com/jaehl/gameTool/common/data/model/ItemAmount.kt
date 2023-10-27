package com.jaehl.gameTool.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemAmount(
    val id : Int = -1,
    val itemId : Int = -1,
    var amount : Int = 0
)