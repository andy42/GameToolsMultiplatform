package com.jaehl.gameTool.apiClientKtor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRecipeRequest(
    val gameId : Int,
    val craftedAt : List<Int>,
    val input : List<RecipeAmountRequest>,
    val output : List<RecipeAmountRequest>
)
