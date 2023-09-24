package com.jaehl.gameTool.common.data.service

import com.jaehl.gameTool.common.data.model.ItemAmount
import com.jaehl.gameTool.common.data.model.Recipe

interface RecipeService {
    fun getRecipes(gameId : Int) : List<Recipe>
    fun getRecipe(recipeId : Int) : Recipe
    fun createRecipes(
        gameId : Int,
        craftedAt : List<Int>,
        input : List<ItemAmount>,
        output : List<ItemAmount>
    ) : Recipe

    fun updateRecipes(
        recipeId : Int,
        gameId : Int,
        craftedAt : List<Int>,
        input : List<ItemAmount>,
        output : List<ItemAmount>
    ) : Recipe

    fun deleteRecipe(recipeId : Int)
}