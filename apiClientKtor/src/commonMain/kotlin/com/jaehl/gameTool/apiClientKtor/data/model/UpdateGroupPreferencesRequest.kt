package com.jaehl.gameTool.apiClientKtor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateGroupPreferencesRequest(
    val showBaseIngredients: Boolean,
    val collapseIngredients: Boolean,
    val costReduction: Float,
    val itemRecipePreferenceMap : Map<Int, Int?>
)
