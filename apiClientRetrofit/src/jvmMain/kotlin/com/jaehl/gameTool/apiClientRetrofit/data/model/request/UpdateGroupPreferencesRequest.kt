package com.jaehl.gameTool.apiClientRetrofit.data.model.request

data class UpdateGroupPreferencesRequest(
    val showBaseIngredients: Boolean,
    val collapseIngredients: Boolean,
    val costReduction: Float,
    val itemRecipePreferenceMap : Map<Int, Int?>
)
