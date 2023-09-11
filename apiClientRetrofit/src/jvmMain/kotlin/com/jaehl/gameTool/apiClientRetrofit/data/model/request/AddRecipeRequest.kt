package com.jaehl.gameTool.apiClientRetrofit.data.model.request

data class AddRecipeRequest(
    val gameId : Int,
    val craftedAt : List<Int>,
    val input : List<RecipeAmountRequest>,
    val output : List<RecipeAmountRequest>
)
