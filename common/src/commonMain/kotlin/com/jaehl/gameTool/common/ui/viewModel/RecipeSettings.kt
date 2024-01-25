package com.jaehl.gameTool.common.ui.viewModel

enum class RecipeDisplayType (val displayName : String, value : Int){
    Normal("Normal", 0),
    BaseItems("Base Items", 1),
    FlattenedList("Flattened List", 2)
}

data class RecipeSettings(
    val displayType : RecipeDisplayType,
    val collapseIngredients : Boolean
)
