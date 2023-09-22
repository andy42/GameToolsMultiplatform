package com.jaehl.gameTool.apiClientRetrofit.data.model.request

data class UpdateRecipeRequest(
    val gameId : Int,
    val craftedAt : List<Int>,
    val input : List<RecipeAmountRequest>,
    val output : List<RecipeAmountRequest>
)
