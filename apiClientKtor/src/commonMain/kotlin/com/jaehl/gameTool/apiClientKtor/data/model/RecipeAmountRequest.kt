package com.jaehl.gameTool.apiClientKtor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeAmountRequest(
    val itemId : Int,
    val amount : Int
)
